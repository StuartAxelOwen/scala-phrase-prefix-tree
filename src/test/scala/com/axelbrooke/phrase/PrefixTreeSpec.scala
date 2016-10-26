package com.axelbrooke.phrase

import org.scalatest.{FunSpec, Matchers}

import scala.reflect.io.File


class PrefixTreeSpec extends FunSpec with Matchers {

  describe("constructing") {
    it("should build from raw string") {
      val tree = PrefixTree.fromString(
        "root\n\tchild1\n\tchild2\n\t\tchild3")

      tree.size should be(4)
      tree.isPhrase(Seq("root", "child2", "child3")) should be(true)
      tree.isPhrase(Seq("child1", "child2")) should be(false)
    }

    describe("from path") {
      val tmpFilePath = "src/test/resources/temp-tree.txt"
      File(tmpFilePath).writeAll("root1\n\tchild1\n\tchild2\nroot2")
      val tree = PrefixTree.from(tmpFilePath)
      tree.size should be(4)
      tree.isPhrase(Seq("root1", "child1")) should be(true)
      tree.isPhrase(Seq("root1", "child2")) should be(true)
      tree.isPhrase(Seq("child1", "child2")) should be(false)
      tree.isPhrase(Seq("cats", "child2")) should be(false)
      tree.get(Seq("cats", "dogs")) should be(None)
      tree.get(Seq("root2")).get.size should be(1)
      tree.get(Seq("root1")).get.size should be(3)
    }

    describe("empty trees") {
      it("from word should work") {
        PrefixTree.leaf("done").size should equal(1)
        PrefixTree.leaf("done") should equal(PrefixTree.leaf("done"))
      }
    }
  }

  describe("transforming phrases") {
    it("should transform basic phrases") {
      // TODO - implement feature fulfilling these tests
//      val tree = PrefixTree.fromString("r\n\ta\n\tb")
//      tree.transform(Seq("r", "a")).head should be("r a")
//      tree.transform(Seq("r", "b")).head should be("r b")
//      tree.transform(Seq("a", "b")).head should be("a")
    }

    describe("longest vs first phrase") {
      it("should pick longest postible phrases when configured") {
        // TODO
      }

      it("should pick the first phrase when configured") {
        // TODO
      }
    }
  }

  describe("detecting phrases") {
    it("should find basic phrases") {
      val tree = PrefixTree.fromString("r\n\ta\n\tb")
      val expected = Set("r b")
      val phrases = tree.phrasesIn(Seq("a", "r", "b", "c", "cats")).toSet
      phrases should equal(expected)
    }

    it("should detect inner phrases") {
      val tree = PrefixTree.fromString("r\n\ta\n\tb\n\t\tc\nb\n\tc")
      val expected = Set("r b", "b c", "r b c")
      val phrases = tree.phrasesIn(Seq("a", "r", "b", "c", "cats")).toSet
      phrases should equal(expected)
    }
  }

}
