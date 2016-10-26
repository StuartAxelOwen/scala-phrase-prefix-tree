package com.axelbrooke.phrase

import org.scalatest.{FunSpec, Matchers}


class TreeItemSpec extends FunSpec with Matchers {
  describe("parsing") {
    it("parses root items") {
      val treeItem = TreeItem.fromLine("root")
      treeItem.term should equal("root")
      treeItem.depth should equal(0)
    }

    it("parses child items") {
      val treeItem = TreeItem.fromLine("\t\tchild")
      treeItem.term should equal("child")
      treeItem.depth should equal(2)
    }
  }
}
