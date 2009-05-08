package com.google.gwt.user.rebind.rpc;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

public class XDRServiceInterfaceProxyGenerator extends Generator {

	 @Override
	  public String generate(TreeLogger logger, GeneratorContext ctx,
	      String requestedClass) throws UnableToCompleteException {
		 
		 logger.log(TreeLogger.WARN,"Running XDRServiceInterfaceProxyGenerator",null);
		 
	    TypeOracle typeOracle = ctx.getTypeOracle();
	    assert (typeOracle != null);

	    JClassType remoteService = typeOracle.findType(requestedClass);
	    if (remoteService == null) {
	      logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
	          + requestedClass + "'", null);
	      throw new UnableToCompleteException();
	    }

	    if (remoteService.isInterface() == null) {
	      logger.log(TreeLogger.ERROR, remoteService.getQualifiedSourceName()
	          + " is not an interface", null);
	      throw new UnableToCompleteException();
	    }

	    XDRProxyCreator proxyCreator = new XDRProxyCreator(remoteService);

	    TreeLogger proxyLogger = logger.branch(TreeLogger.DEBUG,
	        "Generating client proxy for remote service interface '"
	            + remoteService.getQualifiedSourceName() + "'", null);

	    return proxyCreator.create(proxyLogger, ctx);
	  }
}
