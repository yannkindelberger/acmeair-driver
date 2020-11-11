package com.acmeair.jmeter.functions;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class GenerateJWTFunction extends AbstractFunction {

  private static String keyStoreLocation;
  private static String keyStoreType;
  private static String keyStorePassword;
  private static String keyStoreAlias;
  private static String jwtIssuer;
  private static String jwtGroup;
  private static String jwtSubject;

  private static final List<String> DESC = Arrays.asList("generate_jwt");
  private static final String KEY = "__generateJwt";

  private List<CompoundVariable> parameters = Collections.emptyList();

  private static String token = "";
  private static int count = 0;

  public static Algorithm algorithm;

  static {
    if (System.getProperty("JWT.keystore.location") == null) {
      keyStoreLocation = "/keyfile/key.p12";
    } else {
      keyStoreLocation = System.getProperty("JWT.keystore.location");
    }
    if (System.getProperty("JWT.keystore.type") == null) {
      keyStoreType = "PKCS12";
    } else {
      keyStoreType = System.getProperty("JWT.keystore.type");
    }
    if (System.getProperty("JWT.keystore.password") == null) {
      keyStorePassword = "secret";
    } else {
      keyStorePassword = System.getProperty("JWT.keystore.password");
    }
    if (System.getProperty("JWT.keystore.alias") == null) {
      keyStoreAlias = "default";
    } else {
      keyStoreAlias = System.getProperty("JWT.keystore.alias");
    }
    if (System.getProperty("JWT.issuer") == null) {
      jwtIssuer = "http://acmeair-ms";
    } else {
      jwtIssuer = System.getProperty("JWT.issuer");
    }
    if (System.getProperty("JWT.group") == null) {
      jwtGroup= "user";
    } else {
      jwtGroup = System.getProperty("JWT.group");
    }
    if (System.getProperty("JWT.subject") == null) {
      jwtSubject = "subject";
    } else {
      jwtSubject = System.getProperty("JWT.subject");
    }


    FileInputStream is;
    try {
      is = new FileInputStream(keyStoreLocation);


      // Get Private Key
      KeyStore keystore = KeyStore.getInstance(keyStoreType);
      keystore.load(is, keyStorePassword.toCharArray());
      Key key = keystore.getKey(keyStoreAlias, keyStorePassword.toCharArray());


      if (key instanceof PrivateKey) {

        // Get public key and create jwt token
        Certificate cert = keystore.getCertificate(keyStoreAlias);       
        RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();        
        algorithm = Algorithm.RSA256(publicKey,(RSAPrivateKey) key);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public String execute(SampleResult arg0, Sampler arg1) throws InvalidVariableException {


    if (count > 0 && token !="") {
      count = (count + 1) % 50;
      //System.out.println("count is not 0, returning" + token);
      return token;
    }
    count = (count + 1) % 10;


    try {
      LocalDateTime now = LocalDateTime.now();
      Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
      Date date = Date.from(instant);

      LocalDateTime plusHour = LocalDateTime.now().plusHours(1);
      Instant instantPlusHour = plusHour.atZone(ZoneId.systemDefault()).toInstant();
      Date datePlusHour = Date.from(instantPlusHour);

      token = JWT.create()
          .withSubject(jwtSubject)
          .withIssuer(jwtIssuer)
          .withExpiresAt(datePlusHour)
          .withIssuedAt(date)
          .withArrayClaim("groups", new String[]{jwtGroup})
          .withClaim("upn", jwtSubject)
          .withJWTId("jti")
          .withAudience("https://audience.com")
          .sign(algorithm);
    } catch (Exception exception) {

      exception.printStackTrace(); 
    }    
    return token;
  }
  @Override
  public String getReferenceKey() {
    return KEY;
  }

  @Override
  public void setParameters(Collection<CompoundVariable> arg0) throws InvalidVariableException {
    parameters = new ArrayList<CompoundVariable>(arg0);
  }

  public List<String> getArgumentDesc() {
    return DESC;
  }
}
