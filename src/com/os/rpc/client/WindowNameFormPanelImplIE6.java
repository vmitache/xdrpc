package com.os.rpc.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.impl.FormPanelImplHost;

public class WindowNameFormPanelImplIE6 extends WindowNameFormPanelImpl {
	
	@Override
	public native void hookEvents(Element iframe, Element form, FormPanelImplHost listener) /*-{
		if (iframe) {
		  iframe.onreadystatechange = function() {
		    // If there is no __formAction yet, this is a spurious onreadystatechange
		    // generated when the iframe is first added to the DOM.
		    if (!iframe.__formAction)
		      return;

		    if (iframe.readyState == 'complete') {
		      // If the iframe's contentWindow has not navigated to the expected action
		      // url, then it must be an error, so we ignore it.
		      //alert('complete ' + iframe.contentWindow.name + ":" + iframe + ":" + iframe.contentWindow);
		 			if(!iframe.__restoreSameDomain) {
		      	//alert('restore same domain ' + iframe.contentWindow.name);
		 				iframe.__restoreSameDomain = true;
		 				// restore same domain property of iframe to read window.name property
		 				iframe.contentWindow.location = @com.google.gwt.core.client.GWT::getModuleBaseURL()() + "clear.cache.gif";
		 				return;
		 			}
		      listener.@com.google.gwt.user.client.ui.impl.FormPanelImplHost::onFrameLoad()();
		    }
		  };
		}

		form.onsubmit = function() {
		  // Hang on to the form's action url, needed in the
		  // onload/onreadystatechange handler.
		  if (iframe) {
		    iframe.__formAction = form.action;
		    iframe.__restoreSameDomain = false;
		  }
		  return listener.@com.google.gwt.user.client.ui.impl.FormPanelImplHost::onFormSubmit()();
		};
	}-*/;

	@Override
	public native void unhookEvents(Element iframe, Element form) /*-{
		if (iframe)
		  iframe.onreadystatechange = null;
		form.onsubmit = null;
	}-*/;
}
