package com.johnsnowlabs.nlp.annotators.sbd

import org.apache.spark.ml.param.{BooleanParam, IntParam, Params, StringArrayParam}

import scala.collection.mutable.ArrayBuffer

trait SentenceDetectorParams extends Params {

  val useAbbrevations = new BooleanParam(this, "useAbbreviations", "whether to apply abbreviations at sentence detection")

  val useCustomBoundsOnly = new BooleanParam(this, "useCustomBoundsOnly", "whether to only utilize custom bounds for sentence detection")

  val explodeSentences = new BooleanParam(this, "explodeSentences", "whether to explode each sentence into a different row, for better parallelization. Defaults to false.")

  val customBounds: StringArrayParam = new StringArrayParam(
    this,
    "customBounds",
    "characters used to explicitly mark sentence bounds"
  )

  val maxLength: IntParam = new IntParam(this, "maxLength", "length at which sentences will be forcibly split. Defaults to 240")

  setDefault(
    useAbbrevations -> true,
    useCustomBoundsOnly -> false,
    explodeSentences -> false,
    customBounds -> Array.empty[String]
  )

  def setCustomBounds(value: Array[String]): this.type = set(customBounds, value)

  def getCustomBounds: Array[String] = $(customBounds)

  def setUseCustomBoundsOnly(value: Boolean): this.type = set(useCustomBoundsOnly, value)

  def getUseCustomBoundsOnly: Boolean = $(useCustomBoundsOnly)

  def setUseAbbreviations(value: Boolean): this.type = set(useAbbrevations, value)

  def getUseAbbreviations: Boolean = $(useAbbrevations)

  def setExplodeSentences(value: Boolean): this.type = set(explodeSentences, value)

  def getExplodeSentences: Boolean = $(explodeSentences)

  def setMaxLength(value: Int): this.type = set(maxLength, value)

  def getMaxLength: Int = $(maxLength)

  def truncateSentence(sentence: String, maxLength: Int): Array[String] = {
    var currentLength = 0
    val allSentences = ArrayBuffer.empty[String]
    val currentSentence = ArrayBuffer.empty[String]

    def addWordToSentence(word: String): Unit = {
      /** Adds +1 because of the space joining words */
      currentLength += word.length + 1
      currentSentence.append(word)
    }

    sentence.split(" ").foreach(word => {
      if (currentLength + word.length > maxLength) {
        allSentences.append(currentSentence.mkString(" "))
        currentSentence.clear()
        currentLength = 0
        addWordToSentence(word)
      }
      else {
        addWordToSentence(word)
      }
    })
    /** add leftovers */
    allSentences.append(currentSentence.mkString(" "))
    allSentences.toArray
  }

}
