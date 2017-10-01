(ns daviwil.com
  (:require
    path
    moment
    handlebars
    metalsmith
    highlighter
    browser-sync
    [shelljs :as shell]
    [child_process :as child]
    [clojure.string :as string]
    ["metalsmith-feed" :as feed]
    ["metalsmith-tags" :as tags]
    ["metalsmith-drafts" :as drafts]
    ["metalsmith-layouts" :as layouts]
    ["metalsmith-excerpts" :as excerpts]
    ["metalsmith-redirect" :as redirect]
    ["metalsmith-markdown" :as markdown]
    ["metalsmith-permalinks" :as permalinks]
    ["metalsmith-pagination" :as pagination]
    ["metalsmith-collections" :as collections]
    ["metalsmith-collection-metadata" :as metadata])
  (:import [goog.string format]))

(def command (nth (.-argv js/process) 3))

(def output-path (.resolve path "out"))

(defn format-date [date]
  (if date
    (-> moment
      (.utc date "YYYY-MM-DD")
      (.format "MMMM D, YYYY"))
    ""))

(.registerHelper handlebars "display-date" format-date)

(.registerHelper handlebars "recent"
  (fn [items options]
    (def items-found (atom 0))

    (string/join
      "\n"
      (for [item items :while (< @items-found 3)]
        (do
          (swap! items-found inc)
          (.fn options item))))))

(defn collection [pathPattern]
  #js {
    :pattern pathPattern
    :sortBy "date"
    :reverse true
  })

(defn paginate [collectionName collectionUri title]
  #js {
    :perPage 5
    :layout "listing.html"
    :first (str collectionUri "/index.html")
    :path (str collectionUri "/page/:num/index.html")
    :pageMetadata #js {
      :title title
      :rssUri (str collectionUri "/rss.xml")
    }
  })

(defn rss [collectionUri]
  #js {
    :rssUri (str collectionUri "/rss.xml")
  })

(defn get-feed [collectionName & [collectionUri]]
  (def path
    (if collectionUri
      (str collectionUri "/")
      ""))

  #js {
    :collection collectionName
    :destination (str path "rss.xml")
    :limit 20
    :preprocess
      (fn [item]
        (set! (.-date item) (format-date (.-date item)))
        item)
  })

(defn build [callback]
  (js/console.log
    (format
      "### Building pages into %s at %s"
      output-path
      (.toLocaleTimeString (js/Date.))))

  (def site (metalsmith. js/__dirname))

  (-> site
    (.metadata
      #js {
        :site #js {
          :title "daviwil.com"
          :url "https://daviwil.com"
          :author "David Wilson"
          :description "Technology, Creativity, and Life"
          }})
    (.source "content")
    (.destination "out")
    (.clean true))

  (if (not= command "preview")
    (.use site (drafts)))

  (-> site
    (.use
      (collections #js {
        :all (collection #js ["**/*.md" "!*.md"])
        :articles (collection "articles/*.md")
        :videos (collection "videos/**/*.md")
        :videosdevaspect (collection "videos/the-dev-aspect/*.md")
      }))
    (.use
      (metadata #js {
        :collections.articles (rss "articles")
        :collections.videos (rss "videos")
        :collections.videosdevaspect (rss "videos/the-dev-aspect")
      }))
    (.use
      (pagination #js {
        :collections.articles (paginate "articles" "articles" "Articles")
        :collections.videos (paginate "videos" "videos" "Videos")
        :collections.videosdevaspect (paginate "videosdevaspect" "videos/the-dev-aspect" "the_dev_aspect")
      }))
    (.use
      (markdown
       #js {
        :gfm true
        :tables true
        :highlight (highlighter)
      }))
    (.use (excerpts))
    (.use (permalinks #js { :relative false }))
    (.use
      (tags #js {
        :handle "tags",
        :layout "listing.html"
        :path "tags/:tag/index.html"
        :pathPage "tags/:tag/:num/index.html"
        :perPage 5
        :sortBy "date"
      }))
    (.use
      (layouts #js {
        :engine "handlebars"
        :directory "templates"
        :partials "templates/partials"
        :pattern "**/*.html"
        :default "article.html"
      }))
    (.use (feed (get-feed "all")))
    (.use (feed (get-feed "articles" "articles")))
    (.use (feed (get-feed "videos" "videos")))
    (.use (feed (get-feed "videosdevaspect" "videos/the-dev-aspect")))
    (.use
      (redirect #js {
        "/linux/nixos/installing-nixos" "/articles/installing-nixos"
      }))
    (.build callback)
  )
)

(defn preview-site []
  (browser-sync #js {
    :server "out"
    :files #js [ "content/**/*" "templates/*.html"]
    :middleware (fn [req res next] (build next))
  }))

(defn publish-site []
  (.rm shell "-rf" "out")

  (build
    (fn [err]
      (if err
        (js/console.log (str "\n" err "\n"))
        (do
          (js/console.log "\n### Build complete, publishing...\n")

          (doto shell
            (.cd "out")
            (.cp "../CNAME" "."))

          ; Using child_process.execSync because of issues
          ; with using shelljs.exec inside of ClojureScript
          (doto child
            (.execSync "git init")
            (.execSync "git add -A")
            (.execSync "git commit -m \"Publish daviwil.com\"")
            (.execSync "git push -f https://daviwil@github.com/daviwil/daviwil.github.io master"))

          (doto shell
            (.cd "..")
            (.rm "-rf" "out/.git"))

          (js/console.log "### Publish complete!\n"))))))

(case command
  "preview" (preview-site)
  "publish" (publish-site)
  (js/console.log "No command specified. Valid commands are 'preview' and 'publish'.\n"))
