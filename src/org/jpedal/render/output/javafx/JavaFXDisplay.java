/*
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.idrsolutions.com
 * Help section for developers at http://www.idrsolutions.com/java-pdf-library-support/
 *
 * (C) Copyright 1997-2013, IDRsolutions and Contributors.
 *
 * 	This file is part of JPedal
 *
     This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


 *
 * ---------------
 * JavaFXDisplay.java
 * ---------------
 */
package org.jpedal.render.output.javafx;

import org.jpedal.render.output.GenericFontMapper;
import org.jpedal.fonts.PdfFont;
import org.jpedal.fonts.glyph.PdfGlyph;
import org.jpedal.io.ObjectStore;
import org.jpedal.objects.GraphicsState;
import org.jpedal.render.DynamicVectorRenderer;
import org.jpedal.render.ShapeFactory;
import org.jpedal.render.output.FontMapper;
import org.jpedal.render.output.OutputDisplay;
import org.jpedal.utils.LogWriter;
import org.jpedal.utils.StringUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.regex.Pattern;

public class JavaFXDisplay extends OutputDisplay {

    //allow you to swap
    final boolean setFontInCSS=false;

    //final private static String separator = System.getProperty("file.separator");

    //used to track font changes
    private int lastFontTextLength, fontTextLength,lastFontSizeAsString;
    
    private String textWithSpaces,lastTextWithSpaces;

	/** used to give each shape a unique ID */
	private int shapeCount = 0;

	// Root directory of the image
	// private String imagePrefix=null;

	private String divName;

	public JavaFXDisplay( int pageNumber, Point2D midPoint, Rectangle cropBox, boolean addBackground, int defaultSize, ObjectStore newObjectRef) {

        //note set params
        super(pageNumber, midPoint, cropBox, addBackground, defaultSize, newObjectRef,null);

		type = DynamicVectorRenderer.CREATE_JAVAFX;

		// setup helper class for static helper code
		Helper = new org.jpedal.render.output.javafx.JavaFXHelper();

		firstPageName = System.getProperty("org.jpedal.pdf2javafx.firstPageName");

        Pattern p = Pattern.compile("[^a-zA-Z]");
        if (firstPageName != null) {
            if (firstPageName.length() == 0 || p.matcher(firstPageName.substring(0,1)).find()) {
                throw new RuntimeException("org.jpedal.pdf2javafx.firstPageName must begin with a character A-Z or a-z.");
            }
        }
	}

	/**
	 * add footer and other material to complete
	 */
	protected void completeOutput() {

		// flush any cached text before we write out
		flushText();

		if (DEBUG_DRAW_PAGE_BORDER)
			drawPageBorder();

		// No nav bar on single page files.
		boolean onePageFile = false;
		if (endPage == 1)
			onePageFile = true;

		// Page dimensions
		int pageWidth = pageData.getCropBoxWidth(pageNumber), pageHeight = pageData.getCropBoxHeight(pageNumber);

        /**
         * write a header
         */
		writeCustom(TOFILE, "/**");
		writeCustom(TOFILE, "* ===========================================");
		writeCustom(TOFILE, "* Java Pdf Extraction Decoding Access Library");
		writeCustom(TOFILE, "* ===========================================");
		writeCustom(TOFILE, "*");
		writeCustom(TOFILE, "* Project Info:  http://www.idrsolutions.com");
		writeCustom(TOFILE, "*");
		writeCustom(TOFILE, "* generated by JPedal PDF to JavaFX 2.2");
		writeCustom(TOFILE, "*");
		writeCustom(TOFILE, "* --------------------------------------------");
		writeCustom(TOFILE, "* page" + pageNumberAsString + ".java");
		writeCustom(TOFILE, "* --------------------------------------------");
		writeCustom(TOFILE, "* --------------------------------------------");
		writeCustom(TOFILE, "* " + getDate());
		writeCustom(TOFILE, "* --------------------------------------------");
		writeCustom(TOFILE, "*/");
		writeCustom(TOFILE, "");
		writeCustom(TOFILE, "package " + packageName + ';');
		writeCustom(TOFILE, "");
		writeCustom(TOFILE, "");
		writeCustom(TOFILE, "import java.io.File;");
		writeCustom(TOFILE, "import javafx.application.Application;");
		writeCustom(TOFILE, "import javafx.scene.layout.Pane;");
		writeCustom(TOFILE, "import javafx.scene.Scene;");
		writeCustom(TOFILE, "import javafx.scene.Node;");
		writeCustom(TOFILE, "import javafx.scene.paint.Color;");
		writeCustom(TOFILE, "import javafx.scene.shape.*;");
		writeCustom(TOFILE, "import javafx.stage.Stage;");
		writeCustom(TOFILE, "import javafx.collections.ObservableList;");
		writeCustom(TOFILE, "import javafx.scene.text.*;");
		writeCustom(TOFILE, "import javafx.scene.layout.BorderPane;");
		writeCustom(TOFILE, "import javafx.scene.image.Image;");
		writeCustom(TOFILE, "import javafx.scene.image.ImageView;");
		writeCustom(TOFILE, "import javafx.scene.transform.Transform;");
		writeCustom(TOFILE, "import javafx.geometry.VPos;");
		writeCustom(TOFILE, "import javafx.animation.*;");
		writeCustom(TOFILE, "import javafx.util.Duration;");

		// Add imports for navBar functionality
		if (!onePageFile) {
			writeCustom(TOFILE, "import javafx.scene.input.MouseEvent;");
			writeCustom(TOFILE, "import javafx.scene.layout.HBox;");
			writeCustom(TOFILE, "import javafx.scene.effect.*;");
			writeCustom(TOFILE, "import javafx.geometry.Pos;");
			writeCustom(TOFILE, "import javafx.event.EventHandler;");
			writeCustom(TOFILE, "import javafx.event.ActionEvent;");
			writeCustom(TOFILE, "import javafx.scene.control.*;");
			writeCustom(TOFILE, "import java.lang.reflect.Method;");
			writeCustom(TOFILE, "import java.net.URI;");
			writeCustom(TOFILE, "import java.net.URISyntaxException;");
			writeCustom(TOFILE, "import java.util.logging.Level;");
			writeCustom(TOFILE, "import java.util.logging.Logger;");
			writeCustom(TOFILE, "import java.io.IOException;");
		}

		writeCustom(TOFILE, " ");
		writeCustom(TOFILE, " ");
		writeCustom(TOFILE, "public class " +javaFxFileName+" extends Application {");//Default class name
		writeCustom(TOFILE, " ");

		writeCustom(TOFILE, "\tprivate BorderPane root;");
		writeCustom(TOFILE, "\tprivate Stage primaryStage;");
		writeCustom(TOFILE, "\tprivate Scene scene;");
		writeCustom(TOFILE, " ");
		writeCustom(TOFILE, " ");
		writeCustom(TOFILE, "\tpublic static void main(String[] args) {");
		writeCustom(TOFILE, "\t\tApplication.launch(args);");
		writeCustom(TOFILE, "\t}");
		writeCustom(TOFILE, " ");
		writeCustom(TOFILE, "\tpublic void start(Stage primaryStage) { ");
		writeCustom(TOFILE, "\t\tthis.primaryStage = primaryStage;");
//		writeCustom(TOFILE, "\t\tprimaryStage.resizableProperty().set(false);");
		writeCustom(TOFILE, "\t\t");

		writeCustom(TOFILE, "\t\troot = new BorderPane();");
		writeCustom(TOFILE, "\t\tscene = new Scene(root, " + pageWidth  +", "+ (pageHeight+50)+");");

        if(setFontInCSS){
            writeCustom(TOFILE, "\t\tscene.getStylesheets().add(this.getClass().getResource(\""+pageNumberAsString+"/styles.css\").toExternalForm());");
        }


		writeCustom(TOFILE, "\t\t//draw first page");
		writeCustom(TOFILE, "\t\tdrawPage(primaryStage, root, scene); //actual execution of commands");
		writeCustom(TOFILE, "");

		writeCustom(TOFILE, "\t}");

		writeCustom(TOFILE,"\n\tpublic static void drawPage(Stage primaryStage, BorderPane root, Scene scene) {");
		writeCustom(TOFILE, "\t\t");
		writeCustom(TOFILE, "\t\troot.getChildren().clear();");//Make sure nothing is on the screen when outputing the next page. 
		writeCustom(TOFILE, "\t\t");
		writeCustom(TOFILE, "\t\tObservableList<Node> addToGroup;");
		writeCustom(TOFILE, "\t\t");
		writeCustom(TOFILE, "\t\tPane pdfContent = new Pane();");
		writeCustom(TOFILE, "\t\troot.setCenter(pdfContent);");
		writeCustom(TOFILE, "\t\taddToGroup = pdfContent.getChildren();");
		writeCustom(TOFILE, "\t\tcreateTransition(pdfContent);");
		
		writeCustom(TOFILE, "\t\tdrawPage0(addToGroup);");


		if (!onePageFile) {// only print out page number if more than one page
			writeCustom(TOFILE, "\t\tprimaryStage.setTitle(\""+packageName +" page - "+ pageNumber +"\");");
			writeCustom(TOFILE, "\t\taddNav(root, primaryStage, scene);");
		}else{//Set primary stage with updated page numbers if not one page file
			writeCustom(TOFILE, "\t\tprimaryStage.setTitle(\""+packageName+"\");");
			writeCustom(TOFILE, "\t\tprimaryStage.setScene(scene);");
			writeCustom(TOFILE, "\t\tprimaryStage.show();");
			
		}
		writeCustom(TOFILE, "\t}\n");
		
		int methodNum = 0;
		int lineNum = 0;
		while (true) {
			StringBuilder sb = new StringBuilder(50);
			String line;
			boolean now = false;
			for (int ii = 0; ii < 50 && !now; ii++) {
				if (fxScript.size() > lineNum) {
					line = fxScript.get(lineNum);
					if (line.trim().equals("**CHOP**")) {
						now = true;
					} else {
						now = false;
						sb.append(fxScript.get(lineNum));
					}
					lineNum++;
				} else
					break;
			}
			
			writeCustom(TOFILE, "\tprivate static void drawPage" + methodNum + "(ObservableList<Node> addToGroup) {");
			writeCustom(TOFILE, sb.toString());
			
			if (fxScript.size() > lineNum) {
				writeCustom(TOFILE, "\t\tdrawPage" + (++methodNum) + "(addToGroup);\n\t}\n");
			} else {
				break;
			}
		}
		
		writeCustom(TOFILE, "\t}");

		// add shared code for setting size
		writeCustom(TOFILE, "\tprivate static void setTextsize(Text textBox, float requiredWidth) {");
		writeCustom(TOFILE, "\t\tfloat actualWidth=(int) textBox.getLayoutBounds().getWidth();");
		writeCustom(TOFILE, "\t\tfloat dx=requiredWidth-actualWidth;");
		writeCustom(TOFILE, "\t\tfloat scalingNeeded=requiredWidth/actualWidth;");
		writeCustom(TOFILE, "\t\ttextBox.setScaleX(scalingNeeded);");
		writeCustom(TOFILE, "\t\ttextBox.setScaleY(scalingNeeded);");
		writeCustom(TOFILE, "\t\ttextBox.setTranslateX(dx/2);");
		writeCustom(TOFILE, "\t}\n");

		// Creates a fade-in transition between pages
		writeCustom(TOFILE, "\tprivate static void createTransition(Node i){");
		writeCustom(TOFILE, "\t\tFadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(2), i);");
		writeCustom(TOFILE, "\t\tfadeOutTransition.setFromValue(0.0);");
		writeCustom(TOFILE, "\t\tfadeOutTransition.setToValue(3.0);");
		writeCustom(TOFILE, "\t\tfadeOutTransition.play();");
		writeCustom(TOFILE, "\t}\n");

		// Add navBar if pages > 1
		if (!onePageFile) {

			String pageName = "page" + pageNumberAsString;

			if(firstPageName!=null && firstPageName.length()>0 && pageNumber==1)
				pageName = firstPageName;

			// Create navBar
			writeCustom(TOFILE, "\tprivate static void addNav(BorderPane root, Stage primaryStage, Scene scene) {");
			writeCustom(TOFILE, "");
			writeCustom(TOFILE, "\t\t//----- naviBar -----");
			writeCustom(TOFILE, "\t\tHBox naviBar = new HBox();");
			writeCustom(TOFILE, "\t\tnaviBar.setStyle(\"-fx-background-color: #F0F8FF; -fx-text-fill: white;\");");
			writeCustom(TOFILE, "\t\tButton start = makeButtons(\"smstart.gif\",\"Go to first Page\",0, root, primaryStage, scene);");
			writeCustom(TOFILE, "\t\tButton back = makeButtons(\"smback.gif\",\"Go back 1 page\",-1, root, primaryStage, scene);");
			writeCustom(TOFILE, "\t\tButton fback = makeButtons(\"smfback.gif\",\"Go back 10 pages\",-10, root, primaryStage, scene);");
			writeCustom(TOFILE, "\t\tString currentPageAsString = String.valueOf("+pageNumber+");");
			writeCustom(TOFILE, "\t\tTextField currentPage = makeTextField(false, currentPageAsString);");
			writeCustom(TOFILE, "\t\tLabel colon = makeLabel(\":\");");
			writeCustom(TOFILE, "\t\tString numberOfPagesAsString = String.valueOf("+endPage+");");
			writeCustom(TOFILE, "\t\tTextField numberOfPages = makeTextField(false, numberOfPagesAsString);");
			writeCustom(TOFILE, "\t\tButton forward = makeButtons(\"smforward.gif\",\"Go forward 1 page\",1, root, primaryStage, scene);");
			writeCustom(TOFILE, "\t\tButton fforward = makeButtons(\"smfforward.gif\",\"Go forward 10 pages\",10, root, primaryStage, scene);");
			writeCustom(TOFILE, "\t\tButton end = makeButtons(\"smend.gif\",\"Go to last page\",9999, root, primaryStage, scene);");
			writeCustom(TOFILE, "");
			writeCustom(TOFILE, "\t\t//Logo with hyperlink to website");
			writeCustom(TOFILE,"\t\tImage logo= new Image(" + (firstPageName != null && pageNumber == 1 ? firstPageName : "page" + pageNumberAsString) +".class.getResourceAsStream(\""+ '/' + packageName + "/icons/logo.gif\"));");
			writeCustom(TOFILE, "");
			writeCustom(TOFILE, "\t\tHyperlink hyperLinkLogo = new Hyperlink(\"\",new ImageView(logo));");
			writeCustom(TOFILE, "\t\thyperLinkLogo.tooltipProperty().set(new Tooltip(\"Visit our website\"));");
			writeCustom(TOFILE, "\t\thyperLinkLogo.setOnAction(new EventHandler<ActionEvent>() {");
			writeCustom(TOFILE, "\t\t\tpublic void handle(ActionEvent event) {");
			writeCustom(TOFILE, "\t\t\t\ttry {");
			writeCustom(TOFILE, "\t\t\t\t\tjava.awt.Desktop.getDesktop().browse(new URI(\"http://www.idrsolutions.com\"));");
			writeCustom(TOFILE, "\t\t\t\t} catch (Exception ex) {");
			writeCustom(TOFILE, "\t\t\t\t\tLogger.getLogger("+ pageName +".class.getName()).log(Level.SEVERE, null, ex);");
			writeCustom(TOFILE, "\t\t\t\t}");
			writeCustom(TOFILE, "\t\t\t}");
			writeCustom(TOFILE, "\t\t});");
			writeCustom(TOFILE, "");
			writeCustom(TOFILE, "\t\tnaviBar.getChildren().addAll(start, fback, back, currentPage, colon, numberOfPages, forward, fforward, end, hyperLinkLogo);");
			writeCustom(TOFILE, "\t\tnaviBar.setSpacing(5);");
			writeCustom(TOFILE, "\t\tnaviBar.setPrefWidth(" + pageData.getCropBoxWidth(pageNumber) + ");");
			writeCustom(TOFILE, "\t\tnaviBar.setPrefHeight(50);");
			writeCustom(TOFILE, "\t\tnaviBar.setAlignment(Pos.CENTER);");
			writeCustom(TOFILE, "\t\troot.setBottom(naviBar);");
			writeCustom(TOFILE, "\t\tprimaryStage.setScene(scene); ");
			writeCustom(TOFILE, "\t\tprimaryStage.show(); ");
			// close method
			writeCustom(TOFILE, "\t} ");

			// make labels
			writeCustom(TOFILE, "\n\tprivate static Label makeLabel(String text) {");

			writeCustom(TOFILE, "\t\tLabel label = new Label();");
			writeCustom(TOFILE, "\t\tlabel.setText(text);");
			writeCustom(TOFILE, "\t\treturn label;");

			// end of makeLabel method
			writeCustom(TOFILE, "\t}");

			// make TextFields
			writeCustom(TOFILE, "\n\tprivate static TextField makeTextField(boolean check, String text) {");

			writeCustom(TOFILE, "\t\tTextField textfield = new TextField();");
			writeCustom(TOFILE, "\t\ttextfield.setEditable(check);");
			writeCustom(TOFILE, "\t\ttextfield.setAlignment(Pos.CENTER);");
			writeCustom(TOFILE, "\t\ttextfield.setText(text);");
			writeCustom(TOFILE, "\t\ttextfield.setPrefHeight(25);");
			writeCustom(TOFILE, "\t\ttextfield.setPrefWidth(50);");
			writeCustom(TOFILE, "\t\ttextfield.setPromptText(text);");
			writeCustom(TOFILE, "\t\treturn textfield;");

			// end of makeTextfield method
			writeCustom(TOFILE, "\t}");

			// make Buttons
			writeCustom(TOFILE, "\n\tprivate static Button makeButtons(String iconName, String toolTip, final int change, final BorderPane root, final Stage primaryStage, final Scene scene) {");
			writeCustom(TOFILE, "\t\tfinal int currentPageNo = "+pageNumber+",pageCount = "+endPage+ ';');
			writeCustom(TOFILE, "");

			writeCustom(TOFILE, "\t\tButton button = new Button();");
			writeCustom(TOFILE, "\t\tbutton.setTooltip(new Tooltip(toolTip));");
			writeCustom(TOFILE, "\t\t");
			writeCustom(TOFILE,"\t\tImage image= new Image(" + (firstPageName != null && pageNumber == 1 ? firstPageName : "page" + pageNumberAsString) +".class.getResourceAsStream(\""+ '/' + packageName + "/icons/\"+iconName));");
			writeCustom(TOFILE, "\t\tif (image != null) ");
			writeCustom(TOFILE, "\t\t\tbutton.setGraphic(new ImageView(image));");

			writeCustom(TOFILE, "\t\t");
			writeCustom(TOFILE, "\t\tcreatePressedLook(button);");
			writeCustom(TOFILE, "\t\t");
			writeCustom(TOFILE, "\t\t//event handler");
			writeCustom(TOFILE, "\t\tbutton.setOnAction(new EventHandler<ActionEvent>() {");
			writeCustom(TOFILE, "\t\t");
			writeCustom(TOFILE, "\t\t\tpublic void handle(ActionEvent event) {");
			writeCustom(TOFILE, "\t\t\t\tint newPageNo = 0;");
			writeCustom(TOFILE, "\t\t\t\t");
			writeCustom(TOFILE, "\t\t\t\tif (change == 0){ //special case 1st page");
			writeCustom(TOFILE, "\t\t\t\t\tnewPageNo = 1;");
			writeCustom(TOFILE, "\t\t\t\t} else if (change == 9999) {");
			writeCustom(TOFILE, "\t\t\t\t\tnewPageNo = pageCount;");
			writeCustom(TOFILE, "\t\t\t\t} else {");
			writeCustom(TOFILE, "\t\t\t\t\tnewPageNo = currentPageNo + change;");
			writeCustom(TOFILE, "\t\t\t\t}");
			writeCustom(TOFILE, "\t\t\t\t");
			writeCustom(TOFILE, "\t\t\t\t//error check for bounds");
			writeCustom(TOFILE, "\t\t\t\tif (newPageNo < 1) {");
			writeCustom(TOFILE, "\t\t\t\t\tnewPageNo = 1;");
			writeCustom(TOFILE, "\t\t\t\t}else if (newPageNo > pageCount) {");
			writeCustom(TOFILE, "\t\t\t\t\tnewPageNo = pageCount;");
			writeCustom(TOFILE, "\t\t\t\t}");
			writeCustom(TOFILE, "\t\t\t\t");

			// Check to make sure that firstPageName isn't empty
			if (firstPageName != null && firstPageName.length() > 0) {
				writeCustom(TOFILE, "\t\t\t\tString customClassName = \""+packageName+ '.' +firstPageName+"\";");
				writeCustom(TOFILE, "\t\t\t\tboolean customFirstPage = false;");
			}
			writeCustom(TOFILE, "\t\t\t\t");
			writeCustom(TOFILE, "\t\t\t\tString newPageNoAsString = String.valueOf(newPageNo);");

			// Check to make sure that firstPageName isn't empty
			if (firstPageName != null && firstPageName.length() > 0) {
				writeCustom(TOFILE, "\t\t\t\t");
				writeCustom(TOFILE, "\t\t\t\tif(newPageNoAsString.equals(\"1\"))");
				writeCustom(TOFILE, "\t\t\t\t\tcustomFirstPage=true;");
				writeCustom(TOFILE, "\t\t\t\t");
			}
			writeCustom(TOFILE, "\t\t\t\tString maxNumberOfPages = String.valueOf(pageCount);");
			writeCustom(TOFILE, "\t\t\t\tint padding = maxNumberOfPages.length() - newPageNoAsString.length();");
			writeCustom(TOFILE, "\t\t\t\tfor (int ii = 0; ii < padding; ii++) {");
			writeCustom(TOFILE, "\t\t\t\t\tnewPageNoAsString = '0' + newPageNoAsString;");
			writeCustom(TOFILE, "\t\t\t\t}");
			writeCustom(TOFILE, "\t\t\t\t");

			// Check to make sure that firstPageName isn't empty
			if (firstPageName != null && firstPageName.length() > 0) {
				writeCustom(TOFILE, "\t\t\t\t//workout new class from pageNumber");
				writeCustom(TOFILE, "\t\t\t\tString newClass=\"\";");
				writeCustom(TOFILE, "\t\t\t\t");
				writeCustom(TOFILE, "\t\t\t\tif(customFirstPage)");
				writeCustom(TOFILE, "\t\t\t\t\tnewClass=customClassName;");
				writeCustom(TOFILE, "\t\t\t\telse");
				writeCustom(TOFILE, "\t\t\t\t\tnewClass=\""+packageName+".page"+"\"+newPageNoAsString;");
			} else {
				writeCustom(TOFILE, "\t\t\t\t//workout new class from pageNumber");
				writeCustom(TOFILE, "\t\t\t\tString newClass=\""+packageName+".page"+"\"+newPageNoAsString;");
			}
			writeCustom(TOFILE, "\t\t\t\t");
			writeCustom(TOFILE, "\t\t\t\t//create an instance");
			writeCustom(TOFILE, "\t\t\t\ttry {");
			writeCustom(TOFILE, "\t\t\t\t\tClass c = Class.forName(newClass);");
			writeCustom(TOFILE, "\t\t\t\t\tApplication nextPage=(javafx.application.Application)c.newInstance();");
			writeCustom(TOFILE, "");
			writeCustom(TOFILE, "\t\t\t\t\tMethod m = c.getMethod(\"drawPage\", new Class[]{Stage.class, BorderPane.class, Scene.class});");
			writeCustom(TOFILE, "\t\t\t\t\tm.invoke(nextPage, new Object[]{primaryStage, root, scene});");
			writeCustom(TOFILE, "");
			writeCustom(TOFILE, "\t\t\t\t} catch (Exception e) {");
			writeCustom(TOFILE, "\t\t\t\t\te.printStackTrace();");
			writeCustom(TOFILE, "\t\t\t\t}");
			writeCustom(TOFILE, "\t\t\t}");
			writeCustom(TOFILE, "\t\t});");
			writeCustom(TOFILE, "\t\t\t\t");

			writeCustom(TOFILE, "\t\treturn button;");

			// end of makeButtons method
			writeCustom(TOFILE, "\t}");

			// add effects to Buttons
			writeCustom(TOFILE, "\n\tprivate static void createPressedLook(final Button button) {");

			writeCustom(TOFILE, "\t\tfinal DropShadow shadow = new DropShadow();");
			writeCustom(TOFILE, "\t\tshadow.setColor(Color.rgb(41, 36, 33));");
			writeCustom(TOFILE, "\t\t");
			writeCustom(TOFILE, "\t\t//Adding the shadow when the button is clicked");
			writeCustom(TOFILE, "\t\tbutton.addEventHandler(MouseEvent.MOUSE_PRESSED,");
			writeCustom(TOFILE, "\t\t\tnew EventHandler<MouseEvent>() {");
			writeCustom(TOFILE, "\t\t");
			writeCustom(TOFILE, "\t\t\t\t@Override");
			writeCustom(TOFILE, "\t\t\t\tpublic void handle(MouseEvent e) {");
			writeCustom(TOFILE, "\t\t\t\t\tbutton.setEffect(shadow);");
			writeCustom(TOFILE, "\t\t\t\t}");
			writeCustom(TOFILE, "\t\t\t});");
			writeCustom(TOFILE, "\t\t//Removing the shadow when the button is released");
			writeCustom(TOFILE, "\t\tbutton.addEventHandler(MouseEvent.MOUSE_RELEASED,");
			writeCustom(TOFILE, "\t\t\tnew EventHandler<MouseEvent>() {");
			writeCustom(TOFILE, "\t\t");
			writeCustom(TOFILE, "\t\t\t\t@Override");
			writeCustom(TOFILE, "\t\t\t\tpublic void handle(MouseEvent e) {");
			writeCustom(TOFILE, "\t\t\t\t\tbutton.setEffect(null);");
			writeCustom(TOFILE, "\t\t\t\t}");
			writeCustom(TOFILE, "\t\t\t});");

			// end of createPressedLook method
			writeCustom(TOFILE, "\t}");

		}

		// end of file
		writeCustom(TOFILE, "}");
		writeCustom(TOFILE, "");

        /**
         * and write out css if not empty and in file
         */
        if (css.length() > 0) {
            customIO.writeCSS(rootDir, fileName, css);
        }

		customIO.flush();

	}
	
    protected void writeTextPosition(float[] aff, int tx, int ty, float fontScaling) {

        if(aff[0]>0 && aff[3]>0 && aff[1]==0 && aff[2]==0){ //simple case (left to right text)

        	writeCustom(SCRIPT, '\t' +divName+".setX(" + tx + ");");
        	writeCustom(SCRIPT, '\t' +divName+".setY(" + ty + ");");

        }else{  //needs a matrix to handle rotation
            
        	writeCustom(SCRIPT, '\t' +divName+".getTransforms().add(Transform.affine(" + setPrecision(aff[0]/ fontScaling,2) + ',' +
                    setPrecision(aff[1]/ fontScaling,2) + ',' + setPrecision(aff[2]/ fontScaling,2) + ',' +
                    setPrecision(aff[3]/ fontScaling,2) + ',' + tx + ", " + ty + "));");

        }
    }

	protected void writeoutTextAsDiv(float fontScaling) {
		
		if (currentTextBlock.isEmpty()) {
			throw new RuntimeException("writeoutTextAsDiv() called incorrectly.  Attempted to write out text with empty text block use flushText.");
		}

		// uniqueID
		divName = "textBox_" + textID + '_' + pageNumber;
		String text = currentTextBlock.getOutputString(true);
		
		//font
        int adjustedFontSize = (currentTextBlock.getFontSize() + currentTextBlock.getFontAdjustment());

        int altFontSize=currentTextBlock.getAltFontSize();
        int fontCondition=currentTextBlock.getFontCondition();
        if(altFontSize>0){

            switch(fontCondition){
                case 1:
                    if(text.length()>32)
                        adjustedFontSize=altFontSize;
                    break;

                case 2:
                    adjustedFontSize=altFontSize;
                    break;
            }
        }

        int fontSizeAsString= (int)(adjustedFontSize*this.scaling);

        //ignore tiny text
        if(adjustedFontSize<1)
            return;

        textWithSpaces=currentTextBlock.getOutputString(false);
        fontTextLength=textWithSpaces.length();
        
        writeCustom(SCRIPT, "\n\t\tText "+ divName +" = new Text(\"" + tidyQuotes(text) + "\");");
        
        writePosition("", false, fontScaling);


		// add it to the display group so it appears
		writeCustom(SCRIPT, "\taddToGroup.add("+divName+");");//add objects to the group

        if(!setFontInCSS){
            // Set the font weight - update as needed.
            String weight = currentTextBlock.getWeight();
            String javaFxWeight = setJavaFxWeight(weight);

            // set the font style - Regular or italic
            String style = currentTextBlock.getStyle();
    		if (style.equals("normal"))
    			writeCustom(SCRIPT, '\t' +divName+".setFont(Font.font(\""+currentTextBlock.getFont()+"\", FontWeight."+javaFxWeight+", FontPosture.REGULAR,"+ fontSizeAsString+"));");
    		else
    			writeCustom(SCRIPT, '\t' +divName+".setFont(Font.font(\""+currentTextBlock.getFont()+"\", FontWeight."+javaFxWeight+", FontPosture.ITALIC,"+ fontSizeAsString+"));");
        }else{
            writeCustom(SCRIPT, '\t' +divName+".getStyleClass().add(\""+divName+"\");");
            writeCustom(SCRIPT, '\t' +divName+".setId(\""+divName+"\");");

            writeCustom(OutputDisplay.CSS, '#' +divName+" {");

            //Add font size to the div
            writeCustom(OutputDisplay.CSS,"\t-fx-font-family: " +currentTextBlock.getFont()+";");
            writeCustom(OutputDisplay.CSS,"\t-fx-font-size: " + fontSizeAsString + "px;");

            if(textMode == TEXT_INVISIBLE_ON_IMAGE){ //make text invisible
                writeCustom(OutputDisplay.CSS, "\t-fx-fill: rgba(255, 255, 255, 0);");
            }else{ //colour

                writeCustom(OutputDisplay.CSS,"\t-fx-fill:" + rgbToColor(currentTextBlock.getColor()) + ';');
            }

            //close
            writeCustom(OutputDisplay.CSS,"}\n");
        }

//		
//        if(includeJSFontResizingCode){
//            //see if we can write out js to set automatically for best fit
//
//            //we ignore JS in some cases below
//            if(lastFontTextLength<2 && fontTextLength>5 && fontSizeAsString==lastFontSizeAsString){
//                //ignore single char followed by stream of text C ontains
//            }else if(lastFontTextLength>3 && fontSizeAsString==lastFontSizeAsString &&
//                    lastTextWithSpaces.charAt(lastFontTextLength-1)!=' ' &&
//                    lastTextWithSpaces.charAt(lastFontTextLength-2)==' ' && fontTextLength>1 &&
//                    textWithSpaces.charAt(0)!=' '){
//                //ignore if last text ends (space) char and next does not start with (space) (space)C ont
//            }else if((currentTextBlock.getWidth()*scaling)>5)
//            	writeCustom(SCRIPT, "\tsetTextsize("+divName+ ',' +(int)(currentTextBlock.getWidth()*scaling)+");");
//
//            //save for next comparison
//            lastFontTextLength=fontTextLength;
//            lastFontSizeAsString=fontSizeAsString;
//            lastTextWithSpaces=textWithSpaces;
//        }
	
		
		// update color if not black(-14475232)
		if (!setFontInCSS && currentTextBlock.getColor() != -14475232)
			writeCustom(SCRIPT, '\t' +divName+".setFill(Color." + rgbToColor(currentTextBlock.getColor()) + ");");



		textID++;

        ///hack to allow us to sub in later - please just ignore and ask Leon
		writeCustom(SCRIPT, "**CHOP**");
	}

	/**
	 * handle any characters which need conversion or escaping
	 * @param outputString
	 * @return
	 */
	private static String tidyQuotes(String outputString) {
		String newString = ""; // the new clean string that will be returned
		for(int i = 0; i < outputString.length(); i ++) {
			// loop through the original string a character at a time
			char ch = outputString.charAt(i);

			if(i + 1 < outputString.length()) {
				// if there is a character after the current one
				char nextCh = outputString.charAt(i+1);
				if(ch == '\\' && nextCh != '\\') {
					newString += '\\'; // adds a trailing \ to \ prevents illegal character errors
				}
				else if(ch=='\\' && nextCh == '\\') // the next character is a \ so the desired result is \\
					newString += '\\';
			}
			
			else { // end of string
				if(ch == '\\') newString += '\\'; // adds a trailing \ if the string ends in a \ prevents the string from overflowing
			}
			
			// Below are the cases that happen regardless of if its at the end of the string or not
			if(ch=='\r') { 
				newString += "\\r";
				} //  special case, return character
			else if(ch=='\n') {
				newString += "\\n"; //  special case, newline character
			}
			else if(ch=='\"') {
				newString += "\\\""; // special case, for " characters so the string is not prematurely closed
			}
			else
				// normal case, add the original character
				newString += ch;
		}
		return newString;

	}

	// allow user to control various values
	public void setBooleanValue(int key, boolean value) {

		switch (key) {
		case IncludeJSFontResizingCode:
			this.includeJSFontResizingCode = value;
			break;

		default:
			super.setBooleanValue(key, value);
		}
	}

	/**
	 * allow user to set own value for certain tags
	 * Throws RuntimeException
	 * @param type
	 * @param value
	 */
	public void setTag(int type, String value) {

		switch (type) {

		case FORM_TAG:
			tag[FORM_TAG] = value;
			break;

		default:
			super.setTag(type, value);
		}
	}

	/**
	 * not actually needed because all the FX stuff happens via flushText() and FXHelper
	 * but added for development so we can disable
	 */
	public void drawEmbeddedText(float[][] Trm, int fontSize, PdfGlyph embeddedGlyph,
                                 Object javaGlyph, int type, GraphicsState gs,
                                 AffineTransform at, String glyf, PdfFont currentFontData, float glyfWidth)
	{

		// @chika - remove to get text working
//		        if(1==1)
		// return;


		super.drawEmbeddedText(Trm, fontSize, embeddedGlyph,javaGlyph, type,  gs,at,  glyf,  currentFontData, glyfWidth);
	}

	/**
	 * not actually needed because all the FX stuff happens via JavaFXShape()
	 * but added for development so we can disable
	 */
	public void drawShape(Shape currentShape, GraphicsState gs, int cmd) {

//		 if(true)
//		 return;

		super.drawShape(currentShape, gs, cmd);
		writeCustom(SCRIPT, "**CHOP**");
	}

	// save image in array to draw
	public int drawImage(int pageNumber, BufferedImage image, GraphicsState gs, boolean alreadyCached, String name, int optionsApplied, int previousUse) {

		int flag=super.drawImage(pageNumber, image, gs, alreadyCached, name, optionsApplied, previousUse);

		name = StringUtils.makeMethodSafe(name.toLowerCase());

		if (flag == -2) { // returned by Super to show we use

			/**
			 * add in any image transparency.
			 */
			float opacity = gs.getAlpha(GraphicsState.FILL);

            String imName = name + "View";
            writeCustom(SCRIPT, "\n");
            writeCustom(SCRIPT,"\tImage "+name+" = new Image(" + (firstPageName != null && pageNumber == 1 ? firstPageName : "page" + pageNumberAsString) +".class.getResourceAsStream(\""+ '/' + packageName +'/'+ imageName +"\"));");
            writeCustom(SCRIPT,"\tImageView " + imName + " = new ImageView("+name+");");
            writeCustom(SCRIPT, '\t' + imName + ".setImage("+name+");");
            writeCustom(SCRIPT, '\t' + imName + ".setFitWidth("+currentImage[2]+");");
            writeCustom(SCRIPT, '\t' + imName + ".setFitHeight("+currentImage[3]+");");
            writeCustom(SCRIPT, '\t' + imName + ".setX("+currentImage[0]+");");
            writeCustom(SCRIPT, '\t' + imName + ".setY("+currentImage[1]+");");
            if (opacity < 1.0f)
				writeCustom(SCRIPT, '\t' + imName + ".setOpacity(" + opacity + ");");
            writeCustom(SCRIPT, "\taddToGroup.add(" + imName + ");");
            writeCustom(SCRIPT, "**CHOP**");
		}

		return -1;
	}


	public void setOutputDir(String outputDir,String outputFilename, String pageNumberAsString) {

		super.setOutputDir(outputDir, outputFilename, pageNumberAsString);

		// create name as combination of both
		if(pageNumber==1 && firstPageName!=null && firstPageName.length()>0){//If firstPageName is set.
			javaFxFileName = firstPageName;
            fileName=pageNumberAsString;
        }else{
			javaFxFileName = "page" + pageNumberAsString;
            fileName=pageNumberAsString;
        }

		packageName = outputFilename;
		if (packageName.contains(" "))
			packageName = packageName.replaceAll(" ", "_");
	}

	/* setup renderer */
	public void init(int width, int height, int rawRotation, Color backgroundColor) {

		super.init(width, height, rawRotation, backgroundColor);

		/**
		 * create the file or tell user to set
		 */
		if (rootDir == null)
			throw new RuntimeException("Please pass in output_dir (second param if running ExtractpageAsJavaFX");

		try {
            customIO.setupOutput(rootDir+javaFxFileName+".java",false, encodingType[JAVA_TYPE]);
		} catch (Exception e) {
            //tell user and log
            if(LogWriter.isOutput())
                LogWriter.writeLog("Exception: "+e.getMessage());
		}

	}

	/**
	 * Add HTML to correct area so we can assemble later.
	 * Can also be used for any specific code features (ie setting a value)
	 */
	public synchronized void writeCustom(int section, Object str) {

		// System.out.println(output+" "+str);

		switch (section) {

		case TOFILE:
			customIO.writeString(str.toString());
			break;

		case TOP_SECTION:
			topSection.append('\t'); // indent
			topSection.append(str.toString());
			topSection.append('\n');
			break;

		case SCRIPT:
			fxScript.add("\t" + str.toString() + "\n");
			break;

		case FORM:
			form.append(str.toString());
			break;

		case TEXT:

			testDivs.append(str.toString());
			break;

		case CSS:
			css.append(str.toString());
			css.append('\n');
			break;

		case KEEP_GLYFS_SEPARATE:

			try {
				this.writeEveryGlyf = (Boolean) str;
			} catch (Exception e) {
                //tell user and log
                if(LogWriter.isOutput())
                    LogWriter.writeLog("Exception: "+e.getMessage());
			}
			break;

		case SET_ENCODING_USED:

			try {
				this.encodingType = ((String[]) str);
			} catch (Exception e) {
                //tell user and log
                if(LogWriter.isOutput())
                    LogWriter.writeLog("Exception: "+e.getMessage());
			}
			break;

		case JSIMAGESPECIAL:
			if (!jsImagesAdded) {
				writeCustom(TOP_SECTION, str);
				jsImagesAdded = true;
			}
			break;

		// special case used from PdfStreamDecoder to get font data
//		case SAVE_EMBEDDED_FONT:
//
//			// save ttf font data as file
//			Object[] fontData = (Object[]) str;
//			PdfFont pdfFont = (PdfFont) fontData[0];
//			String fontName = pdfFont.getFontName();
//			String fileType = (String) fontData[2];
//
//			// make sure Dir exists
//			String fontPath = rootDir + javaFxFileName + '/';
//			File cssDir = new File(fontPath);
//			if (!cssDir.exists()) {
//				cssDir.mkdirs();
//			}
//
//			try {
//				BufferedOutputStream fontOutput =new BufferedOutputStream(new FileOutputStream(fontPath +fontName+ '.' +fileType));
//				fontOutput.write((byte[]) fontData[1]);
//				fontOutput.flush();
//				fontOutput.close();
//
//			} catch (Exception e) {
//                //tell user and log
//                if(LogWriter.isOutput())
//                    LogWriter.writeLog("Exception: "+e.getMessage());
//			}
//
//			// save details into CSS so we can put in HTML
//			StringBuffer fontTag = new StringBuffer();
//			fontTag.append("@font-face {\n"); // indent
//			fontTag.append("\tfont-family: ").append(fontName).append(";\n");
//			fontTag.append("\tsrc: url(\"").append(javaFxFileName).append('/').append(fontName).append('.').append(fileType).append("\");\n");
//			fontTag.append("}\n");
//
//			writeCustom(OutputDisplay.CSS, fontTag);
//
//			break;

		default:
			super.writeCustom(section, str);
		}
	}

	protected void drawPatternedShape(Shape currentShape, GraphicsState gs) {

		super.drawPatternedShape(currentShape, gs);

		if (currentPatternedShape[0] != -1) {
			writeCustom(SCRIPT, "\n");
			writeCustom(SCRIPT,"\tImage "+shadeId+" = new Image(page" + pageNumberAsString +".class.getResourceAsStream(\""+ '/' + packageName +'/'+ currentPatternedShapeName +"\"));");
			writeCustom(SCRIPT,"\tImageView "+shadeId+"ImageView = new ImageView("+shadeId+");");
			writeCustom(SCRIPT, '\t' + shadeId + "ImageView.setImage(" + shadeId + ");");
			writeCustom(SCRIPT, '\t' +shadeId+"ImageView.setFitWidth("+currentPatternedShape[2]+");");
			writeCustom(SCRIPT, '\t' +shadeId+"ImageView.setFitHeight("+currentPatternedShape[3]+");");
			writeCustom(SCRIPT, '\t' + shadeId + "ImageView.setX(" + currentPatternedShape[0] + ");");
			writeCustom(SCRIPT, '\t' + shadeId + "ImageView.setY(" + currentPatternedShape[1] + ");");
			writeCustom(SCRIPT, "\taddToGroup.add(" + shadeId + "ImageView);");
			writeCustom(SCRIPT, "**CHOP**"); // Output is allowed to make a new method here.
		}

	}



	protected void drawNonPatternedShape(Shape currentShape, GraphicsState gs, int cmd,  String name,Rectangle2D cropBox,Point2D midPoint) {

		ShapeFactory shape = new org.jpedal.render.output.javafx.JavaFXShape(cmd, shapeCount, scaling, currentShape, gs, new AffineTransform(), midPoint, cropBox.getBounds(), currentColor, dpCount, pageRotation, pageData, pageNumber, includeClip);
		shape.setShapeNumber(shapeCount);
		shapeCount++;

		if (!shape.isEmpty()) {
			writeCustom(SCRIPT, shape.getContent());
			writeCustom(SCRIPT, "**CHOP**"); // Safe for method to get split here.

			// update current color
			currentColor = shape.getShapeColor();
		}
	}

	/**
	 * Draws boxes around where the text should be.
	 */
	protected void drawTextArea()
	{
		if (currentTextBlock.isEmpty())
			return;

		int yAdjust = currentTextBlock.getFontSize();


		double[] coords = {currentTextBlock.getX(), (int) currentTextBlock.getY() + yAdjust};
		writeCustom(SCRIPT, "pdf.moveTo(" + coordsToStringParam(coords, 2) + ");");
		coords[0] = currentTextBlock.getX() + currentTextBlock.getWidth();
		writeCustom(SCRIPT, "pdf.lineTo(" + coordsToStringParam(coords, 2) + ");");
		coords[1] = currentTextBlock.getY() - currentTextBlock.getFontSize() + yAdjust;
		writeCustom(SCRIPT, "pdf.lineTo(" + coordsToStringParam(coords, 2) + ");");
		coords[0] = currentTextBlock.getX();
		writeCustom(SCRIPT, "pdf.lineTo(" + coordsToStringParam(coords, 2) + ");");
		writeCustom(SCRIPT, "pdf.closePath();");
		writeCustom(SCRIPT, "pdf.strokeStyle = '" + rgbToColor(200) + "';");
		writeCustom(SCRIPT, "pdf.lineWidth = '1'");
		writeCustom(SCRIPT, "pdf.stroke();");
		writeCustom(SCRIPT, "**CHOP**");
	}

	/**
	 * Draw a debug area around border of page.
	 */
	protected void drawPageBorder() {
		double[] coords = { cropBox.getX(), cropBox.getY() };
		writeCustom(SCRIPT, "pdf.moveTo(" + coordsToStringParam(coords, 2) + ");");
		coords[0] += cropBox.getWidth();
		writeCustom(SCRIPT, "pdf.lineTo(" + coordsToStringParam(coords, 2) + ");");
		coords[1] += cropBox.getHeight();
		writeCustom(SCRIPT, "pdf.lineTo(" + coordsToStringParam(coords, 2) + ");");
		coords[0] -= cropBox.getWidth();
		writeCustom(SCRIPT, "pdf.lineTo(" + coordsToStringParam(coords, 2) + ");");
		writeCustom(SCRIPT, "pdf.closePath();");
		writeCustom(SCRIPT, "pdf.strokeStyle = '" + rgbToColor(0) + "';");
		writeCustom(SCRIPT, "pdf.lineWidth = '1'");
		writeCustom(SCRIPT, "pdf.stroke();");
		writeCustom(SCRIPT, "**CHOP**");

	}

	protected FontMapper getFontMapper(PdfFont currentFontData) {
		return new GenericFontMapper(currentFontData.getFontName(),fontMode,currentFontData.isFontEmbedded);
	}

}