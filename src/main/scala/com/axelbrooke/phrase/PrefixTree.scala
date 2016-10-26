package com.axelbrooke.phrase

import java.io.{ByteArrayInputStream, File, FileInputStream, InputStream}

import scala.io.Source


class PrefixTree(
  val root: String,
  val children: Map[String, PrefixTree],
  firstPhraseWins: Boolean = true
) {

  def get(phrase: Seq[String]): Option[PrefixTree] =
    phrase.headOption match {
      case None => None
      case Some(token) =>
        children.contains(token) match {
          case false => None
          case true => phrase.tail match {
            case tailEmpty if tailEmpty.isEmpty => children.get(token)
            case tailNotEmpty => children(token).get(tailNotEmpty)
          }
        }
    }

  def getOrElse(phrase: Seq[String], default: PrefixTree): PrefixTree =
    children.contains(phrase.head) match {
      case false => default
      case true => children(phrase.head).getOrElse(phrase.tail, default)
    }

  private def itail[T](seq: Seq[T]): Seq[T] = {
    seq.size match {
      case size if size < 2 => Seq.empty
      case size =>
        seq.zipWithIndex.foldLeft(Seq.empty[T])({case (newSeq, (item, idx)) => {
          idx match {
            case progress if progress == seq.size - 1 => newSeq
            case progress => newSeq :+ item
          }
        }})
    }
  }

  private def takeUntilNotPhrase(
    sentence: Seq[String],
    phrases: Seq[Seq[String]] = Seq.empty,
    subtree: PrefixTree = this
  ): Seq[Seq[String]] =
    subtree.children contains sentence.head match {
      case false => phrases
      case true =>
        takeUntilNotPhrase(
          sentence.tail,
          phrases :+ (phrases.lastOption.getOrElse(Seq.empty[String]) :+ sentence.head),
          subtree.children(sentence.head)
        )
    }

  private def phrasesInInner(sentence: Seq[String]): Set[String] =
    sentence.isEmpty match {
      case true => Set.empty
      case false =>
        (
          isPhrase(sentence) match {
            case true => Set(sentence.mkString(" "))
            case false => Set.empty
          }
        ) ++ (
          children.contains(sentence.head) match {
            case false => phrasesInInner(sentence.tail)
            case true => takeUntilNotPhrase(sentence).map(_.mkString(" ")).toSet ++ phrasesInInner(sentence.tail)
          }
        )
    }

  def phrasesIn(sentence: Seq[String], minPhraseSize: Int = 2): Set[String] =
    phrasesInInner(sentence).filter(_.count(_ == ' ') >= minPhraseSize - 1)

  def isPhrase(phrase: Seq[String]): Boolean =
    get(phrase).isDefined

  def hasChildren: Boolean = children.isEmpty

  def transform(sentence: Seq[String]): Seq[String] = {
    null
  }

  def mapTransform(sentences: Seq[Seq[String]]): Seq[Seq[String]] =
    sentences.map(transform)

  override def hashCode(): Int = root.hashCode + children.values.map(_.hashCode()).sum
  def size: Int =
    (isRoot match {
      case true => 0
      case false => 1
    }) + children.values.map(_.size).sum

  def isLeaf: Boolean = children.isEmpty
  def isRoot: Boolean = root == null

  override def equals(obj: scala.Any): Boolean = hashCode() == obj.hashCode()
}


object PrefixTree {

  def depthChunk(treeItems: Seq[TreeItem]): Seq[Seq[TreeItem]] = {
    val baseDepth = treeItems.head.depth
    treeItems.foldLeft(Seq.empty[Seq[TreeItem]])((seqseq, treeItem) => {
      treeItem.depth match {
        case depth if depth == baseDepth => seqseq ++ Seq(Seq(treeItem))
        case depth if depth > baseDepth => seqseq.sliding(2).map(_.head).toSeq  :+ (seqseq.last :+ treeItem)
      }
    })
  }

  def buildTree(root: TreeItem, rest: Seq[TreeItem]): PrefixTree = {
    rest.takeWhile(_.depth > root.depth) match {
      case empty if empty.isEmpty => PrefixTree.leaf(root.term)
      case children =>
        PrefixTree(
          root.term,
          depthChunk(rest)
            .map((items) =>
              items.head.term -> buildTree(items.head, items.tail)
            ).toMap
        )
    }
  }

  def from(stream: InputStream): PrefixTree =
    buildTree(
      TreeItem.root,
      Source.fromInputStream(stream).getLines().toStream.map(TreeItem.fromLine)
    )

  def from(path: String): PrefixTree =
    PrefixTree.from(new FileInputStream(new File(path)))

  def fromString(serializedTree: String): PrefixTree =
    PrefixTree.from(new ByteArrayInputStream(serializedTree.getBytes))

  def leaf(word: String): PrefixTree = PrefixTree(word, Map.empty[String, PrefixTree])

  def apply(root: String, children: Map[String, PrefixTree]): PrefixTree =
    new PrefixTree(root, children)
}
