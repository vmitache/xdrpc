package com.os.rpc.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.impl.FormPanelImpl;
import com.google.gwt.user.client.ui.impl.FormPanelImplHost;

public class WindowNameFormPanelImpl extends FormPanelImpl {
	public native String getContents(Element iframe) /*-{
		try {
		  if (!iframe.contentWindow || !iframe.contentWindow.document)
		    return null;
		  return iframe.contentWindow.name;
		} catch (e) {
		  return null;
		}
	}-*/;

	public native void hookEvents(Element iframe, Element form, FormPanelImplHost listener) /*-{
		if (iframe) {
		  iframe.onload = function() {
		    // If there is no __formAction yet, this is a spurious onload
		    // generated when the iframe is first added to the DOM.
		    if (!iframe.__formAction)
		      return;
				if(!iframe.__restoreSameDomain) {
					iframe.__restoreSameDomain = true;
					// restore same domain property of iframe to read window.name property
					iframe.contentWindow.location = @com.google.gwt.core.client.GWT::getModuleBaseURL()() + "clear.cache.gif";
					return;
				}
		    listener.@com.google.gwt.user.client.ui.impl.FormPanelImplHost::onFrameLoad()();
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
}
