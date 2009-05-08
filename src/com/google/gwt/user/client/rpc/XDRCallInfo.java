package com.google.gwt.user.client.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
public @interface XDRCallInfo {
	public static enum Type {
		GET,POST,POST_GET,POST_GWT
	}
	Type value();
	int timeout() default 15000;
}