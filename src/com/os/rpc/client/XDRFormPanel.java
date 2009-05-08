package com.os.rpc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.impl.FormPanelImplHost;

public class XDRFormPanel extends SimplePanel implements FormPanelImplHost {
	public static class SubmitCompleteEvent extends GwtEvent<SubmitCompleteHandler> {
		/**
		 * The event type.
		 */
		private static Type<SubmitCompleteHandler> TYPE;

		/**
		 * Handler hook.
		 * 
		 * @return the handler hook
		 */
		static Type<SubmitCompleteHandler> getType() {
			if (TYPE == null) {
				TYPE = new Type<SubmitCompleteHandler>();
			}
			return TYPE;
		}

		private String resultHtml;

		/**
		 * Create a submit complete event.
		 * 
		 * @param resultsHtml
		 *          the results from submitting the form
		 */
		protected SubmitCompleteEvent(String resultsHtml) {
			this.resultHtml = resultsHtml;
		}

		@Override
		public final Type<SubmitCompleteHandler> getAssociatedType() {
			return TYPE;
		}

		/**
		 * Gets the result text of the form submission.
		 * 
		 * @return the result html, or <code>null</code> if there was an error
		 *         reading it
		 * @tip The result html can be <code>null</code> as a result of submitting a
		 *      form to a different domain.
		 */
		public String getResults() {
			return resultHtml;
		}

		@Override
		protected void dispatch(SubmitCompleteHandler handler) {
			handler.onSubmitComplete(this);
		}
	}

	/**
	 * Handler for {@link FormPanel.SubmitCompleteEvent} events.
	 */
	public interface SubmitCompleteHandler extends EventHandler {
		/**
		 * Fired when a form has been submitted successfully.
		 * 
		 * @param event
		 *          the event
		 */
		void onSubmitComplete(XDRFormPanel.SubmitCompleteEvent event);
	}

	/**
	 * Fired when the form is submitted.
	 */
	public static class SubmitEvent extends GwtEvent<SubmitHandler> {
		/**
		 * The event type.
		 */
		private static Type<SubmitHandler> TYPE = new Type<SubmitHandler>();

		/**
		 * Handler hook.
		 * 
		 * @return the handler hook
		 */
		static Type<SubmitHandler> getType() {
			if (TYPE == null) {
				TYPE = new Type<SubmitHandler>();
			}
			return TYPE;
		}

		private boolean canceled = false;

		/**
		 * Cancel the form submit. Firing this will prevent a subsequent
		 * {@link FormPanel.SubmitCompleteEvent} from being fired.
		 */
		public void cancel() {
			this.canceled = true;
		}

		@Override
		public final Type<XDRFormPanel.SubmitHandler> getAssociatedType() {
			return TYPE;
		}

		/**
		 * Gets whether this form submit will be canceled.
		 * 
		 * @return <code>true</code> if the form submit will be canceled
		 */
		public boolean isCanceled() {
			return canceled;
		}

		@Override
		protected void dispatch(XDRFormPanel.SubmitHandler handler) {
			handler.onSubmit(this);
		}

	}

	/**
	 * Handler for {@link FormPanel.SubmitEvent} events.
	 */
	public interface SubmitHandler extends EventHandler {
		/**
		 *Fired when the form is submitted.
		 * 
		 * <p>
		 * The FormPanel must <em>not</em> be detached (i.e. removed from its parent
		 * or otherwise disconnected from a {@link RootPanel}) until the submission
		 * is complete. Otherwise, notification of submission will fail.
		 * </p>
		 * 
		 * @param event
		 *          the event
		 */
		void onSubmit(XDRFormPanel.SubmitEvent event);
	}

  public static final String ENCODING_URLENCODED = "application/x-www-form-urlencoded";
  public static final String METHOD_POST = "POST";

  private static int __formId = 0;
  private static WindowNameFormPanelImpl __impl = GWT.create(WindowNameFormPanelImpl.class);
	
  public static XDRFormPanel wrap(Element element) {
    // Assert that the element is attached.
    assert Document.get().getBody().isOrHasChild(element);

    XDRFormPanel formPanel = new XDRFormPanel(element);

    // Mark it attached and remember it for cleanup.
    formPanel.onAttach();
    RootPanel.detachOnWindowClose(formPanel);

    return formPanel;
  }

  public static XDRFormPanel wrap(Element element, boolean createIFrame) {
    // Assert that the element is attached.
    assert Document.get().getBody().isOrHasChild(element);

    XDRFormPanel formPanel = new XDRFormPanel(element, createIFrame);

    // Mark it attached and remember it for cleanup.
    formPanel.onAttach();
    RootPanel.detachOnWindowClose(formPanel);

    return formPanel;
  }

  private String frameName;
  private Element synthesizedFrame;

  public XDRFormPanel() {
    this(Document.get().createFormElement(), true);
  }

  /**
   * Creates a FormPanel that targets a {@link NamedFrame}. The target frame is
   * not physically attached to the form, and must therefore still be added to a
   * panel elsewhere.
   * 
   * <p>
   * When the FormPanel targets an external frame in this way, it will not fire
   * the FormSubmitComplete event.
   * </p>
   * 
   * @param frameTarget the {@link NamedFrame} to be targetted
   */
  public XDRFormPanel(NamedFrame frameTarget) {
    this(frameTarget.getName());
  }

  /**
   * Creates a new FormPanel. When created using this constructor, it will be
   * submitted either by replacing the current page, or to the named
   * &lt;iframe&gt;.
   * 
   * <p>
   * When the FormPanel targets an external frame in this way, it will not fire
   * the FormSubmitComplete event.
   * </p>
   * 
   * @param target the name of the &lt;iframe&gt; to receive the results of the
   *          submission, or <code>null</code> to specify that the current page
   *          be replaced
   */
  public XDRFormPanel(String target) {
    super(Document.get().createFormElement());
    setTarget(target);
    setMethod(METHOD_POST);
    setEncoding(ENCODING_URLENCODED);
  }

  /**
   * This constructor may be used by subclasses to explicitly use an existing
   * element. This element must be a &lt;form&gt; element.
   * 
   * <p>
   * The specified form element's target attribute will not be set, and the
   * {@link FormSubmitCompleteEvent} will not be fired.
   * </p>
   * 
   * @param element the element to be used
   */
  protected XDRFormPanel(Element element) {
    this(element, false);
  }

  /**
   * This constructor may be used by subclasses to explicitly use an existing
   * element. This element must be a &lt;form&gt; element.
   * 
   * <p>
   * If the createIFrame parameter is set to <code>true</code>, then the wrapped
   * form's target attribute will be set to a hidden iframe. If not, the form's
   * target will be left alone, and the FormSubmitComplete event will not be
   * fired.
   * </p>
   * 
   * @param element the element to be used
   * @param createIFrame <code>true</code> to create an &lt;iframe&gt; element
   *          that will be targeted by this form
   */
  protected XDRFormPanel(Element element, boolean createIFrame) {
    super(element);
    FormElement.as(element);
    if (createIFrame) {
      assert ((getTarget() == null) || (getTarget().trim().length() == 0)) : "Cannot create target iframe if the form's target is already set.";
      frameName = "FormPanel_" + (++__formId);
      setTarget(frameName);
      sinkEvents(Event.ONLOAD);
    }
    setMethod(METHOD_POST);
    setEncoding(ENCODING_URLENCODED);
  }
  
  public HandlerRegistration addSubmitCompleteHandler(SubmitCompleteHandler handler) {
    return addHandler(handler, SubmitCompleteEvent.getType());
  }

  /**
   * Adds a {@link SubmitEvent} handler.
   * 
   * @param handler the handler
   * @return the handler registration used to remove the handler
   */
  public HandlerRegistration addSubmitHandler(SubmitHandler handler) {
    return addHandler(handler, SubmitEvent.getType());
  }

  /**
   * Gets the 'action' associated with this form. This is the URL to which it
   * will be submitted.
   * 
   * @return the form's action
   */
  public String getAction() {
    return getFormElement().getAction();
  }

  /**
   * Gets the encoding used for submitting this form. This should be either
   * {@link #ENCODING_MULTIPART} or {@link #ENCODING_URLENCODED}.
   * 
   * @return the form's encoding
   */
  public String getEncoding() {
    return __impl.getEncoding(getElement());
  }

  /**
   * Gets the HTTP method used for submitting this form. This should be either
   * {@link #METHOD_GET} or {@link #METHOD_POST}.
   * 
   * @return the form's method
   */
  public String getMethod() {
    return getFormElement().getMethod();
  }

  /**
   * Gets the form's 'target'. This is the name of the {@link NamedFrame} that
   * will receive the results of submission, or <code>null</code> if none has
   * been specified.
   * 
   * @return the form's target.
   */
  public String getTarget() {
    return getFormElement().getTarget();
  }

  /**
   * Fired when a form is submitted.
   * 
   * @return true if the form is submitted, false if canceled
   */
  public boolean onFormSubmit() {
    UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();
    if (handler != null) {
      return onFormSubmitAndCatch(handler);
    } else {
      return onFormSubmitImpl();
    }
  }

  public void onFrameLoad() {
    UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();
    if (handler != null) {
      onFrameLoadAndCatch(handler);
    } else {
      onFrameLoadImpl();
    }
  }

  public void reset() {
    __impl.reset(getElement());
  }

  /**
   * Sets the 'action' associated with this form. This is the URL to which it
   * will be submitted.
   * 
   * @param url the form's action
   */
  public void setAction(String url) {
    getFormElement().setAction(url);
  }

  /**
   * Sets the encoding used for submitting this form. This should be either
   * {@link #ENCODING_MULTIPART} or {@link #ENCODING_URLENCODED}.
   * 
   * @param encodingType the form's encoding
   */
  private void setEncoding(String encodingType) {
    __impl.setEncoding(getElement(), encodingType);
  }

  /**
   * Sets the HTTP method used for submitting this form. This should be either
   * {@link #METHOD_GET} or {@link #METHOD_POST}.
   * 
   * @param method the form's method
   */
  private void setMethod(String method) {
    getFormElement().setMethod(method);
  }

  /**
   * Submits the form.
   * 
   * <p>
   * The FormPanel must <em>not</em> be detached (i.e. removed from its parent
   * or otherwise disconnected from a {@link RootPanel}) until the submission is
   * complete. Otherwise, notification of submission will fail.
   * </p>
   */
  public void submit() {
    // Fire the onSubmit event, because javascript's form.submit() does not
    // fire the built-in onsubmit event.
    if (!fireSubmitEvent()) {
      return;
    }
    __impl.submit(getElement(), synthesizedFrame);
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    if (frameName != null) {
      // Create and attach a hidden iframe to the body element.
      createFrame();
      Document.get().getBody().appendChild(synthesizedFrame);
      // Hook up the underlying iframe's onLoad event when attached to the DOM.
      // Making this connection only when attached avoids memory-leak issues.
      // The FormPanel cannot use the built-in GWT event-handling mechanism
      // because there is no standard onLoad event on iframes that works across
      // browsers.
      __impl.hookEvents(synthesizedFrame, getElement(), this);
    }
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    if (synthesizedFrame != null) {
      // Unhook the iframe's onLoad when detached.
      __impl.unhookEvents(synthesizedFrame, getElement());

      // And remove it from the document.
      Document.get().getBody().removeChild(synthesizedFrame);
      synthesizedFrame = null;
    }
  }

  // For unit-tests.
  Element getSynthesizedIFrame() {
    return synthesizedFrame;
  }

  private void createFrame() {
    // Attach a hidden IFrame to the form. This is the target iframe to which
    // the form will be submitted. We have to create the iframe using innerHTML,
    // because setting an iframe's 'name' property dynamically doesn't work on
    // most browsers.
    Element dummy = Document.get().createDivElement();
    //dummy.setInnerHTML("<iframe src=\"javascript:''\" name='" + frameName + "' style='position:absolute;width:0;height:0;border:0'>");
    dummy.setInnerHTML("<iframe name='" + frameName + "' style='position:absolute;width:1;height:1;border:0'>");

    synthesizedFrame = dummy.getFirstChildElement();
  }

  /**
   * Fire a {@link FormPanel.SubmitEvent}.
   * 
   * @return true to continue, false if canceled
   */
  private boolean fireSubmitEvent() {
    XDRFormPanel.SubmitEvent event = new XDRFormPanel.SubmitEvent();
    fireEvent(event);
    return !event.isCanceled();
  }

  private FormElement getFormElement() {
    return getElement().cast();
  }

  private boolean onFormSubmitAndCatch(UncaughtExceptionHandler handler) {
    try {
      return onFormSubmitImpl();
    } catch (Throwable e) {
      handler.onUncaughtException(e);
      return false;
    }
  }

  /**
   * @return true if the form is submitted, false if canceled
   */
  private boolean onFormSubmitImpl() {
    return fireSubmitEvent();
  }

  private void onFrameLoadAndCatch(UncaughtExceptionHandler handler) {
    try {
      onFrameLoadImpl();
    } catch (Throwable e) {
      handler.onUncaughtException(e);
    }
  }

  private void onFrameLoadImpl() {
    // Fire onComplete events in a deferred command. This is necessary
    // because clients that detach the form panel when submission is
    // complete can cause some browsers (i.e. Mozilla) to go into an
    // 'infinite loading' state. See issue 916.
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        fireEvent(new SubmitCompleteEvent(__impl.getContents(synthesizedFrame)));
      }
    });
  }

  private void setTarget(String target) {
    getFormElement().setTarget(target);
  }
}