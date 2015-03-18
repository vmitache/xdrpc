develop 'almost standard' GWT remoting with full cross domain benefits .. beware - security and authentication are not addressed yet :D

# Introduction #
> The new module declares a new interface - **XDRRemoteService** (this one simple extends **RemoteService** ) and a couple of new annotations :
  * remote full url of the servlet for cross domain GWT requests
```
@Target(ElementType.TYPE)
public @interface RemoteServiceAbsolutePath {
  String value();
}
```
  * one 'helper' annotation to define the type of cross domain method to be used
```
 @Target({ElementType.TYPE,ElementType.METHOD})
 public @interface XDRCallInfo {
  public static enum Type { GET,POST,POST_GET,POST_GWT }
  Type value();
  int timeout() default 15000;
 }
```

  1. **Type.GET** - for cross scripting using script elements - essentialy a GET method
  1. **Type.POST** - for window.name cross domain POST
  1. **Type.POST\_GET** - for a two step cross domain request - a POST followed by a GET method
  1. **Type.POST\_GWT** - basically NOT A CROSS DOMAIN request - just the regular GWT RPC mode

  * Call types can be mixed inside a single XDRRemoteService interface - some methods may use **GET** some **POST**, and so on..

# Details #

In module.xml

```
   <inherits name='com.os.rpc.XDRRPC'/> 
```
---

  * define the remote interface to extend XDRRemoteService
```
 import com.google.gwt.core.client.GWT;
 import com.google.gwt.user.client.rpc.RemoteServiceAbsolutePath;
 import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
 import com.google.gwt.user.client.rpc.XDRCallInfo;
 import com.google.gwt.user.client.rpc.XDRRemoteService;
 import com.google.gwt.user.client.rpc.XDRCallInfo.Type;

 @RemoteServiceAbsolutePath("http://<cross domain host:port>/<full servlet url>")
 @RemoteServiceRelativePath("test")
 public interface TestXDRService extends XDRRemoteService {
   @XDRCallInfo(value=Type.GET,timeout=10000)
   public String hello_get(String name);

   @XDRCallInfo(value=Type.POST,timeout=10000)
   public String hello_post(String name);

   @XDRCallInfo(value=Type.POST_GET,timeout=10000)
   public String hello_post_get(String name);

   @XDRCallInfo(value=Type.POST_GWT,timeout=10000)
   public String hello_post_gwt(String name);
}

```

  * define the corresponding async interface

```
 import com.google.gwt.user.client.rpc.AsyncCallback;

 public interface TestXDRServiceAsync {
   public void hello_get(String name, AsyncCallback<String> callback);
   public void hello_post(String name, AsyncCallback<String> callback);
   public void hello_post_get(String name, AsyncCallback<String> callback);
   public void hello_post_gwt(String name, AsyncCallback<String> callback);
 }
```

  * simple servlet implementation

```
  import com.os.rpc.server.XDRRemoteServiceServlet;
  import test.client.TestXDRService;

  public class TestXDRRemoteServiceServlet extends XDRRemoteServiceServlet implements TestXDRService {

    public String hello_get(String pName) {
      return "Hello " + pName;
    }

    public String hello_post(String pName) {
      return "Hello " + pName;
    }

    public String hello_post_get(String pName) {
      return "Hello " + pName;
    }

    public String hello_post_gwt(String pName) {
      return "Hello " + pName;
    }
 }
```



> Thanks to
> > http://development.lombardi.com/?p=611

> and
> > http://timepedia.blogspot.com/2008/07/cross-domain-formpanel-submissions-in.html

for very important hints and guidance