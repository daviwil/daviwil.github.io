const path        = require('path');
const Metalsmith  = require('metalsmith');
const feed        = require('metalsmith-feed');
const tags        = require('metalsmith-tags');
const drafts      = require('metalsmith-drafts');
const layouts     = require('metalsmith-layouts');
const excerpts    = require('metalsmith-excerpts');
const redirect    = require('metalsmith-redirect');
const markdown    = require('metalsmith-markdown');
const metadata    = require('metalsmith-collection-metadata');
const permalinks  = require('metalsmith-permalinks');
const pagination  = require('metalsmith-pagination');
const collections = require('metalsmith-collections');
const highlighter = require('highlighter');
const moment      = require('moment')
const Handlebars  = require('handlebars');

const command = process.argv[2];

function formatDate(date) {
  return date ? moment.utc(date, 'YYYY-MM-DD').format("MMMM D, YYYY") : '';
}

Handlebars.registerHelper("display-date", formatDate);

Handlebars.registerHelper("recent", function(items, options) {

  var limit = 5;
  var output = "";
  var itemsFound = 0;

  if (items) {
    for (var i = 0; i < items.length; i++) {
      if (itemsFound >= limit) break;
      output += options.fn(items[i]);
      itemsFound++;
    }
  }

  return output;
});

const outputPath = path.resolve('out');

function collection(pathPattern) {
  return {
    pattern: pathPattern,
    sortBy: 'date',
    reverse: true
  }
}

function paginate(collectionName, collectionUri, title) {
  return {
    perPage: 5,
    layout: 'listing.html',
    first: `${collectionUri}/index.html`,
    path: `${collectionUri}/page/:num/index.html`,
    pageMetadata: {
      title: title,
      rssUri: `${collectionUri}/rss.xml`
    }
  }
}

function rss(collectionUri) {
  return {
    rssUri: `${collectionUri}/rss.xml`
  }
}

function getFeed(collectionName, collectionUri, title) {
  var path = collectionUri ? `${collectionUri}/` : '';
  return {
    collection: collectionName,
    destination: `${path}rss.xml`,
    limit: 20,
    preprocess: function (item) {
      item.date = formatDate(item.date)
      return item
    }
  }
}

function build(callback) {
  console.log(`### Building pages into ${outputPath} at ${new Date().toLocaleTimeString()}`)

  var site =
    Metalsmith(__dirname)
      .metadata({
          site: {
            title: 'daviwil.com',
            url: 'https://daviwil.com',
            author: 'David Wilson',
            description: 'Technology, Creativity, and Life'
          }
      })
      .source('content')
      .destination('out')
      .clean(true);

  if (command !== "preview") {
    site.use(drafts())
  }

  site
    .use(collections({
      all: collection(['**/*.md', '!*.md']),
      articles: collection('articles/*.md'),
      videos: collection('videos/**/*.md'),
      videosdevaspect: collection('videos/the-dev-aspect/*.md')
    }))
    .use(metadata({
      'collections.articles': rss('articles'),
      'collections.videos': rss('videos'),
      'collections.videosdevaspect': rss('videos/the-dev-aspect')
    }))
    .use(pagination({
      'collections.articles': paginate('articles', 'articles', 'Articles'),
      'collections.videos': paginate('videos', 'videos', 'Videos'),
      'collections.videosdevaspect': paginate('videosdevaspect', 'videos/the-dev-aspect', 'the_dev_aspect')
    }))
    .use(markdown({
        gfm: true,
        tables: true,
        highlight: highlighter()
    }))
    .use(excerpts())
    .use(permalinks({
      relative: false
    }))
    .use(tags({
        handle: 'tags',
        layout: 'listing.html',
        path: 'tags/:tag/index.html',
        pathPage: 'tags/:tag/:num/index.html',
        perPage: 5,
        sortBy: "date",
    }))
    .use(layouts({
        engine: 'handlebars',
        directory: 'templates',
        partials: 'templates/partials',
        pattern: '**/*.html',
        default: 'article.html'
    }))
    .use(feed(
        getFeed('all')
    ))
    .use(feed(
        getFeed('articles', 'articles')
    ))
    .use(feed(
        getFeed('videos', 'videos')
    ))
    .use(feed(
        getFeed('videosdevaspect', 'videos/the-dev-aspect')
    ))
    .use(redirect({
      '/linux/nixos/installing-nixos': '/articles/installing-nixos'
    }))
    .build(callback);
}

if (command === "preview") {
  const browserSync = require('browser-sync')

  browserSync({
      server: 'out',
      files: ['content/**/*', 'templates/*.html'],
      middleware: function (req, res, next) {
          build(next);
      }
  })
}
else if (command === "publish") {
  const shell = require('shelljs');

  shell.rm('-rf', 'out');

  build(function(err) {
    if (!err) {
      console.log("### Build complete, publishing...\n");

      shell.cd('out')
      shell.cp('../CNAME', '.')

      shell.exec('git init')
      shell.exec('git add -A')
      shell.exec('git commit -m "Publish daviwil.com"')
      shell.exec('git push -f https://daviwil@github.com/daviwil/daviwil.github.io master')

      shell.cd('..')
      shell.rm('-rf', 'out/.git');

      console.log("\n### Publish complete!\n");
    }
  })
}
else {
  console.log("No command specified. Valid commands are 'preview' and 'publish'.\n");
}
