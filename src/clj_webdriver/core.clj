;; Clojure Wrapper for Selenium-WebDriver
;;
;; WebDriver is a library that allows for easy manipulation of the Firefox,
;; Chrome, Safari and  Internet Explorer graphical browsers, as well as the
;; Java-based HtmlUnit headless browser.
;;
;; This library simply wraps around the core API provided by WebDriver, to
;; include things like navigating to and from URL's, finding and interacting
;; with elements within HTML pages, and handling browser cookies.
;;
;; The vast majority of this code has been taken from mikitebeka's
;; `webdriver-clj` project hosted on Github. I plan to add support for
;; the Select class, which provides easier mechanisms for interacting with
;; select list form elements.
;; 
(ns clj-webdriver.core
  (:use clj-webdriver.util)
  (:import [org.openqa.selenium By WebDriver WebElement Speed Cookie
                                NoSuchElementException]
           [org.openqa.selenium.firefox FirefoxDriver]
           [org.openqa.selenium.ie InternetExplorerDriver]
           [org.openqa.selenium.chrome ChromeDriver]
           [org.openqa.selenium.htmlunit HtmlUnitDriver]
           [org.openqa.selenium.support.ui Select]
           [java.util Date]
           [java.io File]))

(def *drivers* ^{:doc "Drivers that are available via WebDriver"}
  {
   :firefox FirefoxDriver
   :ie InternetExplorerDriver
   :chrome ChromeDriver
   :htmlunit HtmlUnitDriver })
               
(defn new-driver
  "Create new driver instance given a browser type"
  [browser]
  (.newInstance (*drivers* browser)))

(defn get-url
  "Navigate the driver to a given URL"
  [driver url]
  (.get driver url))

(defn start
  "Shortcut to instantiate a driver, navigate to a URL, and return the driver for further use"
  [browser url]
  (let [d (new-driver browser)]
    (do
      (get-url d url)
      d)))

(defn current-url
  "Retrieve the URL of the current page"
  [driver]
  (.getCurrentUrl driver))

(defn title
  "Retrieve the title of the current page as defined in the `head` tag"
  [driver]
  (.getTitle driver))

(defn page-source
  "Retrieve the source code of the current page"
  [driver]
  (.getPageSource driver))

(defn close
  "Close this browser instance"
  [driver]
  (.close driver))

; TODO catch webdriver exception
(defn quit
  "Destroy this browser instance"
  [driver]
  (.quit driver))

(defn window-handles
  "Retrieve a set of window handles which can be used to switchTo particular open windows"
  [driver]
  (into #{} (.getWindowHandles driver)))

(defn window-handle
  "Get the only (or first) window handle"
  [driver]
  (.getWindowHandle driver))

; Navigation interface
(defn back
  "Go back to the previous page in \"browsing history\""
  [driver]
  (.back (.navigate driver)))

(defn forward
  "Go forward to the next page in \"browsing history\"."
  [driver]
  (.forward (.navigate driver)))

(defn to
  "Navigate to a particular URL. Arg `url` can be either String or java.net.URL. Equivalent to the `get` function, provided here for compatibility with WebDriver API."
  [driver url]
  (.to (.navigate driver) url))

(defn refresh
  "Refresh the current page"
  [driver]
  (.refresh (.navigate driver)))

; TargetLocator interface
(defn switch-to-frame
  "Switch focus to a particular HTML frame"
  [driver frame]
  (.frame (.switchTo driver) frame))

(defn switch-to-window
  "Switch focus to a particular open window"
  [driver window]
  (.window (.switchTo driver) window))

(defn switch-to-default
  "Switch focus to the first first frame of the page, or the main document if the page contains iframes"
  [driver]
  (.defaultContent (.switchTo driver)))

(defn swith-to-active
  "Switch to element that currently has focus, or to the body if this cannot be detected"
  [driver]
  (.activeElement (.switchTo driver)))

; FIXME: Full Cookie interface
(defn new-cookie
  "Create a new cookie instance"
  ([name value] (new-cookie name value "/" nil))
  ([name value path] (new-cookie name value path nil))
  ([name value path date] (new Cookie name value path date)))

(defn cookie-name
  "Retrieve the name of a particular cookie"
  [cookie]
  (.getName cookie))

(defn cookie-value
  "Retrieve the value of a particular cookie"
  [cookie]
  (.getValue cookie))

; Option interface
(defn add-cookie
  "Add a new cookie to the browser session"
  [driver cookie]
  (.addCookie (.manage driver) cookie))

; FIXME: Multi method for delete-cookie-named and delete-cookie
(defn delete-cookie-named
  "Delete a cookie given its name"
  [driver name]
  (.deleteCookieNamed (.manage driver) name))

(defn delete-cookie
  "Delete a cookie given a cookie instance"
  [driver cookie]
  (.deleteCookie (.manage driver) cookie))

(defn delete-all-cookies
  "Delete all cookies defined in the current session"
  [driver]
  (.deleteAllCookies (.manage driver)))

(defn cookies
  "Retrieve a set of cookies defined in the current session"
  [driver]
  (into #{} (.getCookies (.manage driver))))

(defn cookie-named
  "Retrieve a cookie object given its name"
  [driver name]
  (.getCookieNamed (.manage driver) name))


(def *slow-speed* Speed/SLOW)
(def *medium-speed* Speed/MEDIUM)
(def *fast-speed* Speed/FAST)

(defn speed
  "Set the speed at which the browser should execute commands"
  ([driver] (.getSpeed (.manage driver)))
  ([driver speed] (.setSpeed (.manage driver) speed)))

(defn by-id
  "Used when finding elements. Returns `By/id` of `expr`"
  [expr]
  (By/id expr))

(defn by-link-text
  "Used when finding elements. Returns `By/linkText` of `expr`"
  [expr]
  (By/linkText expr))

(defn by-partial-link-text
  "Used when finding elements. Returns `By/partialLinkText` of `expr`"
  [expr]
  (By/partialLinkText expr))

(defn by-name
  "Used when finding elements. Returns `By/name` of `expr`"
  [expr]
  (By/name expr))

(defn by-tag-name
  "Used when finding elements. Returns `By/tagName` of `expr`"
  [expr]
  (By/tagName expr))

(defn by-xpath
  "Used when finding elements. Returns `By/xpath` of `expr`"
  [expr]
  (By/xpath expr))

(defn by-class-name
  "Used when finding elements. Returns `By/className` of `expr`"
  [expr]
  (By/className expr))

(defn by-css-selector
  "Used when finding elements. Returns `By/cssSelector` of `expr`"
  [expr]
  (By/cssSelector expr))

(defn find-element
  "Retrieve the element object of an element described by `by`"
  [driver by]
  (try (.findElement driver by)
  (catch NoSuchElementException e nil)))

(defn find-elements
  "Retrieve a vector of element objects described by `by`"
  [driver by]
  (try (into [] (.findElements driver by))
  (catch NoSuchElementException e [])))

; WebElement
(defn click
  "Click a particular HTML element"
  [element]
  (.click element))

(defn submit
  "Submit the form which contains the given element object"
  [element]
  (.submit element))

(defn value
  "Retrieve the `value` attribute of the given element object"
  [element]
  (.getValue element))

(defn clear
  "Clear the contents of the given element object"
  [element]
  (.clear element))

(defn tag-name
  "Retrieve the name of the HTML tag of the given element object"
  [element]
  (.getTagName element))

(defn attribute
  "Retrieve the value of the attribute of the given element object"
  [element attr-name]
  (.getAttribute element attr-name))

(defn toggle
  "If the given element object is a checkbox, this will toggle its selected/unselected state"
  [element]
  (.toggle element))

(defn selected?
  "Returns true if the given element object is selected"
  [element]
  (.isSelected element))

(defn select
  "Select a given element object"
  [element]
  (.setSelected element))

(defn enabled?
  "Returns true if the given element object is enabled"
  [element]
  (.isEnabled element))

(defn text
  "Retrieve the content, or inner HTML, of a given element object"
  [element]
  (.getText element))

(defn send-keys
  "Type the string of keys into the element object"
  [element char-keys]
  (.sendKeys element (into-array CharSequence (list char-keys))))

(def input-text send-keys)

;; org.openqa.selenium.support.ui.Select class

(defn deselect-all
  "Clear all selected entries for select list described by `by`"
  [element]
  (let [select-list (Select. element)]
    (.deselectAll select-list)))

(defn deselect-by-index
  "Deselect the option at index `idx` for the select list described by `by`. Indeces begin at 1"
  [element idx]
  (let [idx-human (dec idx)
        select-list (Select. element)]
    (.deselectByIndex select-list idx-human)))

(defn deselect-by-value
  "Deselect all options with value `value` for the select list described by `by`"
  [element value]
  (let [select-list (Select. element)]
    (.deselectByValue select-list value)))

(defn deselect-by-text
  "Deselect all options with visible text `text` for the select list described by `by`"
  [element text]
  (let [select-list (Select. element)]
    (.deselectByVisibleText select-list text)))

(defn all-selected-options
  "Retrieve all selected options from the select list described by `by`"
  [element]
  (let [select-list (Select. element)]
    (into [] (.getAllSelectedOptions select-list))))

(defn first-selected-option
  "Retrieve the first selected option (or the only one for single-select lists) from the select list described by `by`"
  [element]
  (let [select-list (Select. element)]
    (.getFirstSelectedOption select-list)))

(defn all-options
  "Retrieve all options in the select list described by `by`"
  [element]
  (let [select-list (Select. element)]
    (into [] (.getOptions select-list))))

(defn multiple?
  "Return true if the select list described by `by` allows for multiple selections"
  [element]
  (let [select-list (Select. element)]
    (.isMultiple select-list)))

(defn select-by-index
  "Select an option by its index in the select list described by `by`. Indeces begin at 1."
  [element idx]
  (let [idx-human (dec idx)
        select-list (Select. element)]
    (.selectByIndex select-list idx-human)))

(defn select-by-value
  "Select all options with value `value` in the select list described by `by`"
  [element value]
  (let [select-list (Select. element)]
    (.selectByValue select-list value)))

(defn select-by-text
  "Select all options with visible text `text` in the select list described by `by`"
  [element text]
  (let [select-list (Select. element)]
    (.selectByVisibleText select-list text)))