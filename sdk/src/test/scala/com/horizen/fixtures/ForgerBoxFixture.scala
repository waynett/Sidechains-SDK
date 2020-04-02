package com.horizen.fixtures

import java.util.Random

import com.horizen.box.ForgerBox
import com.horizen.box.data.ForgerBoxData
import com.horizen.secret.PrivateKey25519
import com.horizen.utils
import com.horizen.utils.Ed25519
import com.horizen.vrf.{VRFKeyGenerator, VRFSecretKey}

case class ForgerBoxGenerationMetadata(propositionSecret: PrivateKey25519, rewardSecret: PrivateKey25519, vrfSecret: VRFSecretKey)

object ForgerBoxFixture {
  def generateForgerBox(seed: Long): (ForgerBox, ForgerBoxGenerationMetadata) = {
    val randomGenerator = new Random(seed)
    val byteSeed = new Array[Byte](32)
    randomGenerator.nextBytes(byteSeed)
    val propositionKeyPair: utils.Pair[Array[Byte], Array[Byte]] = Ed25519.createKeyPair(byteSeed)
    val ownerKeys: PrivateKey25519 = new PrivateKey25519(propositionKeyPair.getKey, propositionKeyPair.getValue)
    val value: Long = randomGenerator.nextLong
    val (vrfSecret, vrfPubKey) = VRFKeyGenerator.generate(ownerKeys.bytes())
    val proposition = ownerKeys.publicImage()

    val forgerBoxData = new ForgerBoxData(proposition, value, proposition, vrfPubKey)
    val nonce: Long = randomGenerator.nextLong

    val forgerBox = forgerBoxData.getBox(nonce)
    (forgerBox, ForgerBoxGenerationMetadata(ownerKeys, ownerKeys, vrfSecret))
  }
}