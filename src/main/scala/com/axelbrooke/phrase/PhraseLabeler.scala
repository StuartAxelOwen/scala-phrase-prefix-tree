package com.axelbrooke.phrase

import scala.io.StdIn


object PhraseLabeler extends App {

  val phraseTree = PrefixTree.from(args.head)

  var readLine = StdIn.readLine()

  while (readLine != null) {
    println(readLine)
    println(phraseTree.phrasesIn(readLine.split(" ")))

    readLine = StdIn.readLine()
  }
}
