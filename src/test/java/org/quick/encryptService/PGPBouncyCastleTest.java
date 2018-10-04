package org.quick.encryptService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.crypto.Cipher;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Esteban_Lopez on 9/5/2018.
 */
public class PGPBouncyCastleTest
{


  @Test
  public void findSecretKey() throws  Exception {

    String phrase = "changeit";
    String secring = "secring.gpg";


    Security.addProvider( new BouncyCastleProvider() );
    FileInputStream keyIn = new FileInputStream(new File(readFile(secring)));

    PGPSecretKeyRingCollection keyRingCollection = new PGPSecretKeyRingCollection(
      PGPUtil.getDecoderStream( keyIn ), new JcaKeyFingerprintCalculator() );


    Iterator<PGPSecretKeyRing>  pgpSecretKeyRing = keyRingCollection.getKeyRings();
    PGPSecretKey secreKey = null;
    PGPPrivateKey privateKey = null;


    while(pgpSecretKeyRing.hasNext()) {

      Iterator<PGPSecretKey> secretKeys = pgpSecretKeyRing.next().getSecretKeys();

      while(secretKeys.hasNext()) {
        secreKey = secretKeys.next();
        JcePBESecretKeyDecryptorBuilder builder = new JcePBESecretKeyDecryptorBuilder();
        builder.setProvider( "BC" );
        PBESecretKeyDecryptor decryptor = builder.build( "changeit".toCharArray());
        privateKey = secreKey.extractPrivateKey( decryptor );

      }

      Assert.assertNotNull( privateKey );

    }


  }



  @Test
  public void testDecryptByes() throws Exception {
    boolean correct = true;

    String data = "Pcs79jeJKqoE0OgC7pzEJA==";
    byte[] inputBytes = Base64.decode(data.getBytes());
   // Security.addProvider( new SunJCE() );
    //Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding", "SunJCE");

    Cipher ciphser = Cipher.getInstance("DES", "SunJCE");


    Assert.assertEquals( correct, true );
  }


  @Test
  public void pgpPublicKeyRingGeneration () throws  IOException,PGPException
  {
     boolean yes = true;

    //FileInputStream stream  = readStream("pubring.gpg");
    //armored keys
    //https://superuser.com/questions/1053889/how-can-i-export-my-public-gnupg-keychain-into-one-block
 //   FileInputStream stream  = readStream("public-keys.gpg");
    //no armored
    //byte[] pgpKey = toFileArray( "pubring.gpg" );

    //armored
    byte[] pgpKey = toFileArray( "public-keys_new.gpg" );

    ByteArrayInputStream bais = new ByteArrayInputStream( pgpKey );

    InputStream decoderStream = PGPUtil.getDecoderStream(bais);
     boolean hasHeader = false;
    if (decoderStream instanceof ArmoredInputStream) {
      final ArmoredInputStream armoredInputStream = (ArmoredInputStream) decoderStream;
      String[] headers = armoredInputStream.getArmorHeaders();
      if(headers !=  null) {
        hasHeader = true;
      }
    }
    ArmoredInputStream ais = null;

    if(hasHeader)
      ais = new ArmoredInputStream(bais, true);
    else
      ais = new ArmoredInputStream( bais, false );

    PGPPublicKeyRingCollection ringsRings = new PGPPublicKeyRingCollection( ais, new JcaKeyFingerprintCalculator() );

    PGPPublicKeyRing pkr = new PGPPublicKeyRing( ais, new JcaKeyFingerprintCalculator() );

    final PGPObjectFactory objectFactory = new PGPObjectFactory(ais,
      new JcaKeyFingerprintCalculator() );

    Object o ;
    while((o = objectFactory.nextObject()) != null) {
      if( o instanceof  ArmoredInputStream) {
        ArmoredInputStream ais2 = new ArmoredInputStream( bais, true );
        boolean cool = true;


      } else if( o instanceof PGPPublicKeyRingCollection ) {
        PGPPublicKeyRingCollection ringRings = new PGPPublicKeyRingCollection( PGPUtil.getDecoderStream( bais ), new JcaKeyFingerprintCalculator() );
        boolean cool = true;
      }

    }



    PGPPublicKeyRingCollection ringRings = new PGPPublicKeyRingCollection( PGPUtil.getDecoderStream( bais ), new JcaKeyFingerprintCalculator() );

    //no armored keys
    FileInputStream stream2 = readStream("pubring.gpg");
// byte[] pubRing  = readFile("pubring.gpg");
// ArmoredInputStream ais = new ArmoredInputStream(PGPUtil.getDecoderStream(stream), false);

    //if keys are amored then you need to specify true in order to check headers
    //ArmoredInputStream ais2 = new ArmoredInputStream(stream, true);



//    PGPPublicKeyRingCollection collection = new PGPPublicKeyRingCollection(
 //     PGPUtil.getDecoderStream(ais),
  //    new JcaKeyFingerprintCalculator());

    PGPPublicKeyRingCollection collection2 = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(stream2), new JcaKeyFingerprintCalculator());
    //PGPPublicKeyRingCollection collection2 = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(ais2),
    //       new JcaKeyFingerprintCalculator());

  }


  public byte[] toFileArray(String fileName) throws IOException {
    return IOUtils.toByteArray( readStream( fileName ));
  }

  public FileInputStream readStream(String fileName) throws IOException {

    return new FileInputStream(this.getClass().getClassLoader().getResource( fileName ).getFile());

  }


  public String readFile(String filename) {
    return  this.getClass().getClassLoader().getResource( filename ).getFile();
  }

  @Test
  public void testPGPPublicKeyRingGeneration() throws Exception {
    PGPPublicKeyRingCollection ringCollection =   generateTest();

    byte[] pgpKeyRaw = ringCollection.getEncoded();
    ByteArrayInputStream bais = new ByteArrayInputStream( pgpKeyRaw );


    try( ArmoredInputStream ais = new ArmoredInputStream( bais, false )){

      PGPPublicKeyRingCollection publicKeyRingCollection = new PGPPublicKeyRingCollection(
        ais, new JcaKeyFingerprintCalculator() );

      PGPPublicKey pk = null;
      Iterator<PGPPublicKeyRing> pgpPublicKeyRingIterator = publicKeyRingCollection.getKeyRings();

      while(pgpPublicKeyRingIterator.hasNext()) {
        PGPPublicKeyRing pgpPublicKeyRing = pgpPublicKeyRingIterator.next();
        Iterator<PGPPublicKey> pgpPublicKeyIterator = pgpPublicKeyRing.iterator();

        while(pgpPublicKeyIterator.hasNext()) {
          pk = pgpPublicKeyIterator.next();
          if(pk.isEncryptionKey()) {
            break;
          }
        }
      }

    }

    boolean test = false;

    Assert.assertNotNull(test);
  }

  public PGPPublicKeyRingCollection generateTest() throws Exception {
    Security.addProvider( new BouncyCastleProvider());

    char[] passPhrase = "changeit".toCharArray();
    KeyPairGenerator dsaKpg = KeyPairGenerator.getInstance( "DSA", "BC" );

    KeyPair dsaKp = dsaKpg.generateKeyPair();

    PGPKeyPair dsaKeyPair = new JcaPGPKeyPair( PGPPublicKey.DSA, dsaKp, new Date( ));
    PGPDigestCalculator sha1 = new JcaPGPDigestCalculatorProviderBuilder()
      .build()
      .get( HashAlgorithmTags.SHA1 );

    PGPKeyRingGenerator keyRingGenerator = new PGPKeyRingGenerator(
      PGPSignature.POSITIVE_CERTIFICATION,
      dsaKeyPair,
      "test",
      sha1,
      null,
      null,
      new JcaPGPContentSignerBuilder( dsaKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1 ),
      new JcePBESecretKeyEncryptorBuilder( PGPEncryptedData.AES_256, sha1 ).setProvider( "BC" ).build( passPhrase ));


    List<PGPPublicKeyRing> publicKeyRingList = Arrays.asList(keyRingGenerator.generatePublicKeyRing());
    return new PGPPublicKeyRingCollection(publicKeyRingList);

  }
}




