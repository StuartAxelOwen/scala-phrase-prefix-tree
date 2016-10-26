package com.axelbrooke.phrase


case class TreeItem(term: String, depth: Int)

object TreeItem {
  def fromLine(line: String): TreeItem = TreeItem(line.trim(), line.lastIndexOf('\t') + 1)
  def root: TreeItem = TreeItem(null, -1)
}
