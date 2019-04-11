package com.acmeair.jmeter.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class GenerateJWTSimple extends AbstractFunction {

  private static final String secretKey = "acmeairsecret128";
  

  private static final List<String> DESC = Arrays.asList("generate_jwt_simple");
  private static final String KEY = "__generateJwtSimple";

  private List<CompoundVariable> parameters = Collections.emptyList();

  public String execute(SampleResult arg0, Sampler arg1) throws InvalidVariableException {
    String token = null;
    try {
      
      String customerid = parameters.get(0).execute();
      
      
      Algorithm algorithm = Algorithm.HMAC256(secretKey);
      token = JWT.create().withSubject(customerid).sign(algorithm);
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
