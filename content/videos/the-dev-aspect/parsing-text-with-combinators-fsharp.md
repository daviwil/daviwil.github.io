---
title: "Parsing Text with Combinators - F# Part 5"
date: 2016-09-22
tags: functional-programming, programming-languages, fsharp
---

<div class="embed-responsive embed-responsive-16by9">
  <iframe class="embed-responsive-item" src="//www.youtube.com/embed/ARJB8eDyxrg?rel=0" allowfullscreen></iframe>
</div>

### *Join the discussion by commenting on the [YouTube page](https://www.youtube.com/watch?v=ARJB8eDyxrg) for this video!*

In this episode we start building a set of parser combinators which can parse the adventure game's text commands in a functional way.  We'll also cover topics like recursive descent parsers, left-associativity, and F#'s range operator.

You can download the code for this episode at https://github.com/thedevaspect/aspect-fsharp

Sections:

- What is a combinator? [0:26]
- Recursive Descent Parser [0:44]
- Defining Command Structure [3:35]
- Defining 'expectChar' [4:51]
- Decomposing a List [5:38]
- The 'or' combinator [10:45]
- Left-associative operators [15:04]
- The 'choice' combinator [15:44]
- The 'anyOf' combinator [17:40]
- The range operator [19:06]
- The 'and' combinator [20:02]
- Defining 'expectString' [25:02]
- Defining the Parser Type [28:21]
- Defining 'runParser' [32:48]
- Defining 'sequenceParsers' [39:46]
- Tail Recursion [40:31]
- The 'map' combinator [46:04]
- The 'apply' combinator [48:45]
- The 'return' combinator [51:13]
- The 'lift' combinator [52:40]

Links:

- Check out Scott Wlaschin's excellent parser combinator series: https://fsharpforfunandprofit.com/series/understanding-parser-combinators.html
- FParsec: http://www.quanttec.com/fparsec/
- Fira Code Font: https://github.com/tonsky/FiraCode
- Parser combinator: https://en.wikipedia.org/wiki/Parser_combinator
- Recursive descent parser: https://en.wikipedia.org/wiki/Recursive_descent_parser
