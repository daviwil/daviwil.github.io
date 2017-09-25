---
title: "Functional Concurrency with the Actor Model - F# Part 4"
date: 2016-08-31
tags: functional-programming, programming-languages, fsharp
layout: video.html
youtube: AMjcjXIMzmA
---

In this episode we learn about the Actor Model and use F#'s MailboxProcessor to create a game loop for our text-based adventure game.  We'll also briefly cover a few more F# features like recursive functions, computation expressions, classes and exception handling.

You can download the code for this episode at https://github.com/thedevaspect/aspect-fsharp

Sections:

- Introducing the Actor Model [00:33]
- Introducing the MailboxProcessor [1:28]
- Creating a MailboxProcessor [3:35]
- Recursive Functions [4:19]
- Computation Expressions [4:52]
- Defining a Message Type [5:51]
- Updating the World State [7:13]
- Sending a Message [10:57]
- Defining a GameEngine class [14:00]
- Concurrent MailboxProcessors [16:31]
- Catching Exceptions [17:38]
- Making the Player Walk [18:09]
- Resetting the World State [23:31]

Links:
- Actor model on Wikipedia: https://en.wikipedia.org/wiki/Actor_model
- Akka.net: http://getakka.net/
- Microsoft Orleans: https://dotnet.github.io/orleans/
