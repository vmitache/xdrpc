package com.os.rpc.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RPCServletUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.os.rpc.client.XDRRpcServiceProxy;

@SuppressWarnings("serial")
public class XDRRemoteServiceServlet extends RemoteServiceServlet {
  private static Random m_rnd = new Random();

	public static class FlowException extends Error {
		public FlowException(String content) {
			super(content);
		}
	}
	
  public void init() throws ServletException {
  	ServerCache.init(getServletConfig());
  }
	
	protected void doGetImpl(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/// User defined GET request
		resp.setContentType("text/html");
		PrintWriter pw = resp.getWriter();
		pw.println("<html><body><h1>your stuff here</h1></body></html>");
		pw.flush();
		pw.close();
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (req.getParameter(XDRRpcServiceProxy.RPC_ID_PARAM) != null || req.getParameter(XDRRpcServiceProxy.RPC_PAYLOAD_PARAM) != null) {
			doPost(req,resp);
		} else {
			doGetImpl(req, resp);
		}
	}

	protected String readContent(HttpServletRequest request) throws ServletException, IOException {
		if("GET".equals(request.getMethod())) {
			if (request.getParameter(XDRRpcServiceProxy.RPC_ID_PARAM) != null) {
				String id = request.getParameter(XDRRpcServiceProxy.RPC_ID_PARAM);
				throw new FlowException(id);
			} else {
				return request.getParameter(XDRRpcServiceProxy.RPC_PAYLOAD_PARAM);
			}
		} else {
			if("application/x-www-form-urlencoded".equals(request.getContentType())) {
				String callType = request.getParameter(XDRRpcServiceProxy.RPC_CALL_TYPE);
				String payload = request.getParameter(XDRRpcServiceProxy.RPC_PAYLOAD_PARAM);
				if(callType == null || payload == null) {
					throw new ServletException("Invalid call");
				}
				request.setAttribute("callType", callType);
				return payload;
			} else {
				return RPCServletUtils.readContentAsUtf8(request,false);
			}
		}
	}

  protected void onAfterResponseSerialized(String serializedResponse) {
  	if("GET".equals(getThreadLocalRequest().getMethod()) || "application/x-www-form-urlencoded".equals(getThreadLocalRequest().getContentType())) {
  		throw new FlowException(serializedResponse);
  	}
  }
  
  protected static String generateResponseId() {
  	long ctime = System.currentTimeMillis();
  	return "ABCDEFGHI".charAt(m_rnd.nextInt(9)) + String.valueOf(ctime);
  }
  
  private String escapeSingleQuotes(String pInput) {
  	if(pInput == null) {
  		return "";
  	}
  	String result = pInput.replaceAll("'", "\\\\'");
  	//System.out.println("escape:" + pInput + ":" + result);
  	return result;
  }
	
	protected void doUnexpectedFailure(Throwable e) {
		if(e instanceof FlowException) {
			HttpServletRequest request = getThreadLocalRequest();
			String responseStr = null;
			try {
				if("GET".equals(request.getMethod())) {
					if (request.getParameter(XDRRpcServiceProxy.RPC_ID_PARAM) != null) {
						responseStr = ServerCache.get(e.getMessage(),String.class);
						//responseStr = (String)getThreadLocalRequest().getSession().getAttribute(e.getMessage());
						if(responseStr == null) {
							super.doUnexpectedFailure(new Exception("Invalid request :" + e.getMessage()));
							return;
						}
					} else {
						responseStr = e.getMessage();
					}
					String callback = request.getParameter("callback");
					responseStr = callback + "('" + escapeSingleQuotes(responseStr) + "');";
					getThreadLocalResponse().setContentType("text/javascript");
					PrintWriter pw = getThreadLocalResponse().getWriter();
					pw.println(responseStr);
					pw.flush();
					pw.close();
				} else {
					if("POST_GET".equals(request.getAttribute("callType"))) {
						responseStr = generateResponseId();
						while(!ServerCache.safeSet(responseStr,e.getMessage(),"30s")) {
							responseStr = generateResponseId();
						}
					} else {
						responseStr = e.getMessage();
					}
					String htmlStr = "<html><body><script type='text/javascript'>window.name='" + escapeSingleQuotes(responseStr) + "';</script></body></html>";
					getThreadLocalResponse().setContentType("text/html");
					PrintWriter pw = getThreadLocalResponse().getWriter();
					pw.println(htmlStr);
					pw.flush();
					pw.close();
				}
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		} else {
			super.doUnexpectedFailure(e);
		}
	}
}
