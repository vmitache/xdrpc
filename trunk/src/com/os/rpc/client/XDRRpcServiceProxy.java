package com.os.rpc.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.impl.Serializer;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.os.rpc.client.XDRFormPanel.SubmitCompleteEvent;

public class XDRRpcServiceProxy extends RemoteServiceProxy {
	public static final String RPC_PAYLOAD_PARAM = "rpcpayload";
	public static final String RPC_CALL_TYPE = "rpccalltype";
	public static final String RPC_ID_PARAM = "rpcid";

	static boolean isReturnValue(String encodedResponse) {
		return encodedResponse != null && encodedResponse.startsWith("//OK");
	}

	static boolean isThrownException(String encodedResponse) {
		return encodedResponse != null && encodedResponse.startsWith("//EX");
	}

	private String m_gwtServiceEntryPoint = null;
	private String m_xdrServiceEntryPoint = null;
	
	protected XDRRpcServiceProxy(String pModuleBaseURL,String pRemoteServiceRelativePath,String pRemoteServiceAbsolutePath,
			String pSerializationPolicyName, Serializer pSerializer) {
		super(pModuleBaseURL, pRemoteServiceRelativePath, pSerializationPolicyName, pSerializer);
		m_gwtServiceEntryPoint = super.getServiceEntryPoint();
		m_xdrServiceEntryPoint = pRemoteServiceAbsolutePath;
	}

	protected <T> Request doInvoke(final ResponseReader responseReader, String methodName, int invocationCount,
			String requestData, final String callType,final int timeout, final AsyncCallback<T> callback) {
		if ("POST".equals(callType) || "POST_GET".equals(callType)) {
			super.setServiceEntryPoint(m_xdrServiceEntryPoint);
			final XDRFormPanel fp = new XDRFormPanel();
			fp.setAction(super.getServiceEntryPoint());
			VerticalPanel vp = new VerticalPanel();
			Hidden hdnField = new Hidden(RPC_PAYLOAD_PARAM, requestData);
			vp.add(hdnField);
			hdnField = new Hidden(RPC_CALL_TYPE, callType);
			vp.add(hdnField);
			fp.add(vp);
			fp.addSubmitCompleteHandler(new XDRFormPanel.SubmitCompleteHandler() {
				public void onSubmitComplete(SubmitCompleteEvent pEvent) {
					String content = pEvent.getResults();
					fp.removeFromParent();
					if ("POST".equals(callType)) {
						try {
							if (isReturnValue(content)) {
								callback.onSuccess((T) responseReader.read(createStreamReader(content)));
							} else if (isThrownException(content)) {
								callback.onFailure((Throwable) responseReader.read(createStreamReader(content)));
							} else {
								callback.onFailure(new InvocationException("Unknown return type"));
							}
						} catch (SerializationException ex) {
							callback.onFailure(new InvocationException("Failure deserializing object " + ex));
						}
					} else {
						doGet2(getRequestId(), content, m_xdrServiceEntryPoint ,timeout,
								new AsyncCallback<String>() {
									public void onFailure(Throwable pCaught) {
										callback.onFailure(new InvocationException("Unable to initiate asynchronous service invocation"));
									}

									public void onSuccess(String pResult) {
										try {
											if (isReturnValue(pResult)) {
												callback.onSuccess((T) responseReader.read(createStreamReader(pResult)));
											} else if (isThrownException(pResult)) {
												callback.onFailure((Throwable) responseReader.read(createStreamReader(pResult)));
											} else {
												callback.onFailure(new InvocationException("Unknown return type"));
											}
										} catch (SerializationException ex) {
											callback.onFailure(new InvocationException("Failure deserializing object " + ex));
										}
									}
								});
					}
				}
			});
			fp.setVisible(true);
			RootPanel.get().add(fp);
			fp.submit();
		} else if("POST_GWT".equals(callType)) {
			super.setServiceEntryPoint(m_gwtServiceEntryPoint);
			return super.doInvoke(responseReader, methodName, invocationCount, requestData, callback);
		} else {
			super.setServiceEntryPoint(m_xdrServiceEntryPoint);
			doGet1(getRequestId(), requestData, m_xdrServiceEntryPoint,timeout, new AsyncCallback<String>() {
				public void onFailure(Throwable pCaught) {
					callback.onFailure(new InvocationException("Unable to initiate asynchronous service invocation"));
				}

				public void onSuccess(String pResult) {
					try {
						if (isReturnValue(pResult)) {
							callback.onSuccess((T) responseReader.read(createStreamReader(pResult)));
						} else if (isThrownException(pResult)) {
							callback.onFailure((Throwable) responseReader.read(createStreamReader(pResult)));
						} else {
							callback.onFailure(new InvocationException("Unknown return type :'" + pResult + "'"));
						}
					} catch (SerializationException ex) {
						callback.onFailure(new InvocationException("Failure deserializing object " + ex));
					}
				}
			});
		}
		return null;
	}

	private native void doGet2(int requestId, String correlationId, String url,int timeout, AsyncCallback handler) /*-{
		var callback = "callback" + requestId;
		var script = $doc.createElement("script");
		script.setAttribute("src",url + "?" + @com.os.rpc.client.XDRRpcServiceProxy::RPC_ID_PARAM + "=" + correlationId + "&callback=" + callback);
		script.setAttribute("type","text/javascript");
		$wnd[callback] = function(strObj) {
			handler.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(strObj);
			$wnd[callback + "done"] = true;
		};
		setTimeout(function() {
			if(!$wnd[callback + "done"]) {
				handler.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(null);
			}
			$doc.body.removeChild(script);
			if($wnd[callback]) {
				try {
					delete $wnd[callback];
				} catch(err) {
					$wnd[callback] = null;
				}
			}
			if($wnd[callback + "done"]) {
				try {
					delete $wnd[callback + "done"];
				} catch(err) {
					$wnd[callback + "done"] = null;
				}
			}
		},timeout);
		$doc.body.appendChild(script);
	}-*/;

	private native void doGet1(int requestId, String payload, String url,int timeout, AsyncCallback handler) /*-{
		var callback = "callback" + requestId;
		var script = $doc.createElement("script");
		script.setAttribute("src",url + "?"  + @com.os.rpc.client.XDRRpcServiceProxy::RPC_PAYLOAD_PARAM + "=" + encodeURIComponent(payload) + "&callback=" + callback);
		script.setAttribute("type","text/javascript");
		$wnd[callback] = function(strObj) {
			handler.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(strObj);
			$wnd[callback + "done"] = true;
		};
		setTimeout(function() {
			if(!$wnd[callback + "done"]) {
				handler.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(null);
			}
			$doc.body.removeChild(script);
			if($wnd[callback]) {
				try {
					delete $wnd[callback];
				} catch(err) {
					$wnd[callback] = null;
				}
			}
			if($wnd[callback + "done"]) {
				try {
					delete $wnd[callback + "done"];
				} catch(err) {
					$wnd[callback + "done"] = null;
				}
			}
		},timeout);
		$doc.body.appendChild(script);
	}-*/;
}
