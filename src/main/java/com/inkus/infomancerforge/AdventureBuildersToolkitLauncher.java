package com.inkus.infomancerforge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;
import com.formdev.flatlaf.extras.components.FlatCheckBox;
import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatLabel.LabelType;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.inkus.infomancerforge.beans.Config;
import com.inkus.infomancerforge.beans.Project;
import com.inkus.infomancerforge.display.factories.DynamicListCellRenderer;
import com.inkus.infomancerforge.editor.AdventureEditor;
import com.inkus.infomancerforge.editor.TermsFrame;
import com.inkus.infomancerforge.editor.wizards.NewProjectWizard;

public class AdventureBuildersToolkitLauncher extends JFrame {
	static private final Logger log = LogManager.getLogger(AdventureBuildersToolkitLauncher.class);
	private static final long serialVersionUID = 1L;

	private JList<Project> projectList;
	private Project selectedProject = null;
	private OpenAction openAction;
	private Config config;

	public AdventureBuildersToolkitLauncher() {
		super("Informancer Forge");
		config = StorageUtilities.getConfig();
		
		setSize(800, 600);
		setResizable(false);
		setLocationRelativeTo(null);
		setUndecorated(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		setIconImages(ImageUtilities.getImages(new String[] {"images/icon256.png","images/icon128.png","images/icon96.png","images/icon64.png","images/icon48.png","images/icon32.png","images/icon24.png","images/icon16.png"}));
		
//		String smallicon="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\"><svg width=\"100%\" height=\"100%\" viewBox=\"0 0 1846 1932\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\" xmlns:serif=\"http://www.serif.com/\" style=\"fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;\"><g><g><path d=\"M1444.23,358.144l378.765,-355.038c14.118,-6.397 22.817,-3.591 22.332,14.814l-328.139,386.81l-72.958,-46.586Z\" style=\"fill:#fff;\"/><path d=\"M1485.11,295.888l-83.67,55.353c40.072,24.016 102.458,67.419 136.759,99.352l43.371,-80.402c-19.96,-39.853 -52.154,-64.567 -96.46,-74.303Z\" style=\"fill:#fff;\"/></g><path d=\"M1213.33,1738.08l117.695,34.102l39.477,-30.936l-688.601,-215.02l707.092,195.333l17.994,-15.715l-405.531,-124.092l420.653,111.626l10.08,-11.513l3.432,-31.301l37.806,-420.396l30.797,11.157l-63.557,653.9l-50.644,36.581l-119.219,-52.739l-334.101,-229.982l276.627,88.995Zm-276.627,-88.995l334.101,229.982l-588.006,-234.625l-0.222,-77.212l254.127,81.855Zm-416.778,-182.371l85.136,74.494l-37.132,35.873l38.076,72.061l54.754,-21.712l-12.587,-73.95l34.404,13.75l0.333,115.734l-92.489,32.964l-89.431,-151.014l16.111,-19.479l-71.311,-59.205l74.136,-19.516Zm-74.136,19.516l-405.386,-331.501l-40.4,-106.594l559.703,-369.902l-453.81,320.559l386.284,348.323l-388.843,-322.065l2.501,14.974l487.305,433.022l-478.932,-392.291l12.02,58.476l393.694,327.483l-74.136,19.516Zm1027.64,-256.062l13.203,-192.807l-213.446,-26.315l242.043,14.852l0.559,10.982l-11.562,204.445l-30.797,-11.157Z\" style=\"fill:#fff;\"/><g><path d=\"M650.151,368.515l-118.36,914.493l326.038,221.663l536.348,-655.322l217.64,-425.158l-415.035,-303.684l-546.631,248.008Zm32.692,69.44c-1.19,0.009 -2.191,0.894 -2.346,2.073l-59.472,452.363c-0.062,0.472 0.019,0.952 0.232,1.377l39.482,78.965c0,0 -51.186,29.25 -51.186,29.25c-0.624,0.356 -1.054,0.975 -1.17,1.684l-39.744,242.113c-0.061,0.378 -0.032,0.764 0.087,1.128l16.151,49.39c0.165,0.504 0.493,0.939 0.933,1.235l209.218,141.182c0.357,0.241 0.774,0.381 1.205,0.404l66.928,3.605c0.812,0.044 1.59,-0.33 2.065,-0.99l162.482,-226.242c0.373,-0.52 0.522,-1.169 0.412,-1.8l-9.696,-55.751c0,-0 75.981,-9.804 75.981,-9.804c0.649,-0.084 1.236,-0.431 1.621,-0.96l201.191,-275.846c0.376,-0.516 0.53,-1.161 0.427,-1.791l-13.635,-83.229c0,0 79.08,6.863 79.08,6.863c0.9,0.078 1.767,-0.359 2.239,-1.129l70.396,-114.809c0.326,-0.531 0.431,-1.169 0.292,-1.776l-18.617,-81.755c-0,-0 68.102,4.833 68.102,4.833c0.941,0.067 1.834,-0.428 2.276,-1.262l68.329,-129.066c0.263,-0.498 0.342,-1.074 0.222,-1.624l-17.026,-78.002c-0.124,-0.57 -0.454,-1.075 -0.926,-1.419l-311.501,-226.835c-0.352,-0.256 -0.766,-0.411 -1.199,-0.448l-77.538,-6.678c-0.429,-0.037 -0.86,0.043 -1.248,0.231l-155.062,75.457c-0.799,0.389 -1.315,1.19 -1.34,2.079l-2.44,88.968c-0,0 -97.68,-35.022 -97.68,-35.022c-0.598,-0.214 -1.256,-0.181 -1.83,0.092l-159.265,75.808c-0.577,0.275 -1.019,0.769 -1.228,1.373c-0.209,0.603 -0.168,1.265 0.116,1.838l36.285,73.322c-0,0 -81.603,0.605 -81.603,0.605Zm2.112,4.753l83.335,-0.617c0.821,-0.006 1.582,-0.435 2.013,-1.134c0.43,-0.7 0.471,-1.572 0.106,-2.308l-36.9,-74.566c-0,-0 156.183,-74.342 156.183,-74.342c0,0 99.844,35.798 99.844,35.798c0.72,0.258 1.52,0.155 2.151,-0.276c0.631,-0.431 1.017,-1.139 1.038,-1.903l2.491,-90.812c-0,-0 153.169,-74.537 153.169,-74.537c-0,0 76.22,6.565 76.22,6.565c13.923,10.139 294.015,214.102 310.231,225.911c0,-0 16.647,76.263 16.647,76.263c-0,-0 -67.194,126.921 -67.194,126.921c-0,0 -69.76,-4.951 -69.76,-4.951c-0.754,-0.053 -1.488,0.253 -1.979,0.826c-0.492,0.574 -0.683,1.346 -0.515,2.082l19.116,83.944c-0,-0 -69.118,112.724 -69.118,112.724c0,-0 -80.666,-7.001 -80.666,-7.001c-0.734,-0.064 -1.455,0.215 -1.955,0.755c-0.501,0.539 -0.724,1.28 -0.605,2.006l13.969,85.271c0,-0 -199.998,274.211 -199.998,274.211c-0,0 -77.435,9.992 -77.435,9.992c-0.645,0.083 -1.228,0.426 -1.614,0.95c-0.386,0.524 -0.542,1.182 -0.43,1.823l9.951,57.22c0,0 -161.136,224.369 -161.136,224.369c-0,-0 -64.966,-3.499 -64.966,-3.499c0,-0 -207.992,-140.355 -207.992,-140.355c-0,-0 -15.716,-48.06 -15.716,-48.06c-0,0 39.465,-240.418 39.465,-240.418c0,-0 52.159,-29.805 52.159,-29.805c1.099,-0.628 1.515,-2.005 0.95,-3.137l-40.165,-80.329c2.635,-20.045 59.106,-449.581 59.106,-449.581l-0,0Zm28.937,32.983l70.156,-0.593l468.666,311.366l12.972,80.883l-185.56,255.873l-88.185,13.784l11.7,66.025l-155.379,215.314l-50.49,2.717l-190.036,-124.29l-14.276,-50.218l36.92,-223.323l62.72,-42.782l-37.446,-94.536l58.238,-410.22Zm483.435,-251.909l106.962,499.741l25.057,-2.321l37.518,-48.334l-19.289,-141.966l112.575,4.622l37.861,-69.742l-0.437,-41.951l-272.707,-199.582l-27.54,-0.467Zm-365.998,194.235l-2.758,-40.78l63.895,-30.385l161.854,66.76l4.338,-147.914l59.676,-37.421l86.838,434.604l-373.843,-244.864Z\" style=\"fill:#fff;\"/><path d=\"M713.872,473.307c-1.18,0.01 -2.175,0.881 -2.341,2.049l-58.237,410.22c-0.059,0.41 -0.009,0.828 0.144,1.213l36.742,92.763c0,-0 -61.144,41.707 -61.144,41.707c-0.538,0.367 -0.903,0.939 -1.009,1.581l-36.919,223.323c-0.058,0.347 -0.038,0.703 0.059,1.041l14.276,50.218c0.157,0.552 0.508,1.029 0.988,1.344l190.036,124.29c0.424,0.277 0.927,0.412 1.433,0.385l50.49,-2.717c0.72,-0.039 1.384,-0.402 1.806,-0.986l155.379,-215.313c0.377,-0.523 0.526,-1.177 0.414,-1.812l-11.276,-63.629c0,-0 85.781,-13.408 85.781,-13.408c0.628,-0.098 1.19,-0.442 1.563,-0.956l185.56,-255.873c0.372,-0.513 0.524,-1.152 0.424,-1.778l-12.973,-80.883c-0.105,-0.657 -0.48,-1.24 -1.035,-1.608l-468.665,-311.366c-0.397,-0.264 -0.863,-0.403 -1.34,-0.399l-70.156,0.594Zm2.093,4.751l67.373,-0.57c18.345,12.189 445.324,295.859 467.187,310.384c-0,0 12.65,78.876 12.65,78.876c0,-0 -184.397,254.269 -184.397,254.269c0,0 -87.206,13.631 -87.206,13.631c-0.633,0.099 -1.199,0.449 -1.572,0.97c-0.372,0.522 -0.519,1.171 -0.407,1.802l11.524,65.032c0,-0 -154.121,213.571 -154.121,213.571c-0,-0 -48.572,2.614 -48.572,2.614c-0,0 -188.638,-123.375 -188.638,-123.375c0,-0 -13.886,-48.848 -13.886,-48.848c-0,0 36.666,-221.79 36.666,-221.79c-0,0 61.877,-42.207 61.877,-42.207c0.921,-0.629 1.284,-1.812 0.873,-2.848l-37.212,-93.949l57.861,-407.562Z\" style=\"fill:#fff;\"/><path d=\"M1022.84,622.454l-323.231,736.782\" style=\"fill:none;\"/><path d=\"M760.894,1170.25l-63.97,185.077l19.2,12.111l83.763,-173.976l-38.993,-23.212Zm111.33,-322.098l72.025,45.472l119.409,-248.013l-98.836,-65.363l-92.598,267.904Z\" style=\"fill:#fff;\"/></g></g></svg>";
////		String medicon="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\"><svg width=\"100%\" height=\"100%\" viewBox=\"0 0 2254 1985\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\" xmlns:serif=\"http://www.serif.com/\" style=\"fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;\"><g><path d=\"M318.085,834.469l404.065,-101.016l-11.885,-41.595c-150.07,35.931 -281.724,82.912 -392.18,142.611Z\" style=\"fill:#fff;\"/><g><path d=\"M1485.18,421.147l291.216,-272.973c10.855,-4.919 17.543,-2.761 17.17,11.389l-252.292,297.403l-56.094,-35.819Z\" style=\"fill:#fff;\"/><path d=\"M1516.61,373.281l-64.331,42.559c30.81,18.465 78.776,51.836 105.149,76.387l33.346,-61.817c-15.346,-30.641 -40.099,-49.643 -74.164,-57.129Z\" style=\"fill:#fff;\"/></g><path d=\"M1387.65,1477.51l50.259,116.364c58.257,21.75 81.492,39.594 127.585,60.836l65.375,-61.676l-21.453,-15.992l40.155,-1.652l10.895,-10.278c-64.217,-27.01 -166.385,-56.014 -272.816,-87.602Zm-254.613,20.438l-54.165,-68.111l-13.043,43.525c31.137,9.028 32.405,13.732 67.208,24.586Zm-272.916,-164.474l-77.811,18.754c48.08,43.503 74.263,66.931 79.434,75.593c7.229,-1.161 22.975,1.605 59.634,10.501l5.1,-69.986c-18.416,-0.881 -29.957,-0.979 -41.133,0.483c-7.35,-14.868 -13.501,-18.395 -25.224,-35.345Zm-330.54,-207.283c72.193,55.241 99.878,86.613 169.892,147.549l87.988,-38.079c-18.466,-20.843 -33.159,-26.63 -54.575,-48.146l-203.305,-61.324Zm-223.151,-189.748l144.61,-0.438c-73.408,-54.325 -105.897,-67.404 -153.186,-85.079l36.164,48.85l-32.853,-5.329l5.265,41.996Z\" style=\"fill:#fff;\"/><path d=\"M1730.72,1224.11l-55.755,561.802l-50.644,36.582l-119.22,-52.739l-57.474,-140.987l117.696,34.102l75.962,-66.338l-15.268,-16.305l30.389,3.839l10.08,-11.513l3.433,-31.301l26.486,-317.244l34.315,0.102Zm-559.72,315.663l127.032,149.556l-262.057,-112.09l23.404,-79.607l111.621,42.141Zm-416.778,-182.371l85.136,74.493l-37.132,35.874l38.076,72.061l54.755,-21.713l-12.588,-73.949l34.404,13.75l0.333,115.734l-92.488,32.964l-89.432,-151.014l16.112,-19.479l-71.312,-59.205l74.136,-19.516Zm-147.792,-41.297l-331.729,-270.688l193.27,61.518l206.286,179.785l-67.827,29.385Zm1101.3,-195.25l14.77,-121.876l19.977,-8.613l-3.951,141.647l-30.796,-11.158Z\" style=\"fill:#fff;\"/><path d=\"M1583.89,724.025l304.857,-162.333l-258.159,245.765l623.404,-182.282l-647.429,285.221l472.204,-12.327l-637.665,161.622l532.922,150.874l-691.156,13.191l285.535,760.438l-493.107,-570.487l-153.172,415.29l10.491,-496.423l-14.178,0.342l-10.374,-26.126l-657.59,180.46l567.689,-257.517l255.825,172.385l461.561,-505.383l48.342,-172.71Zm-716.581,-131.768l-54.421,610.949l-812.884,-256.362l797.827,-1.558l-268.386,-945.286l337.864,592.257Zm162.573,-146.853l-129.596,55.99l-4.521,36.302l-64.32,-384.633l198.437,292.341Z\" style=\"fill:#fff;\"/><g><path d=\"M913.924,511.601l-91.002,703.115l250.677,170.428l412.375,-503.851l167.335,-326.886l-319.103,-233.49l-420.282,190.684Zm25.136,53.389c-0.915,0.007 -1.685,0.687 -1.804,1.594l-45.725,347.803c-0.048,0.363 0.014,0.731 0.177,1.059l30.357,60.713c-0,-0 -39.355,22.488 -39.355,22.488c-0.48,0.274 -0.81,0.75 -0.9,1.295l-30.557,186.151c-0.047,0.29 -0.025,0.587 0.067,0.867l12.418,37.974c0.126,0.387 0.379,0.722 0.717,0.95l160.859,108.548c0.275,0.186 0.595,0.294 0.926,0.311l51.458,2.772c0.625,0.033 1.223,-0.254 1.588,-0.762l124.925,-173.947c0.288,-0.4 0.402,-0.899 0.318,-1.384l-7.455,-42.865c-0,-0 58.418,-7.538 58.418,-7.538c0.499,-0.064 0.95,-0.331 1.247,-0.738l154.687,-212.087c0.289,-0.396 0.407,-0.892 0.328,-1.376l-10.483,-63.991c-0,-0 60.801,5.276 60.801,5.276c0.692,0.06 1.359,-0.276 1.722,-0.868l54.124,-88.272c0.251,-0.408 0.331,-0.898 0.225,-1.365l-14.314,-62.859c-0,0 52.36,3.717 52.36,3.717c0.724,0.051 1.411,-0.329 1.75,-0.971l52.536,-99.233c0.202,-0.383 0.263,-0.826 0.17,-1.249l-13.09,-59.972c-0.096,-0.439 -0.349,-0.827 -0.712,-1.091l-239.5,-174.404c-0.27,-0.197 -0.589,-0.316 -0.922,-0.345l-59.616,-5.134c-0.33,-0.029 -0.661,0.033 -0.959,0.178l-119.221,58.015c-0.614,0.299 -1.011,0.916 -1.03,1.599l-1.876,68.403c-0,0 -75.102,-26.926 -75.102,-26.926c-0.46,-0.165 -0.966,-0.14 -1.407,0.07l-122.452,58.286c-0.444,0.211 -0.784,0.591 -0.945,1.055c-0.161,0.464 -0.128,0.973 0.089,1.414l27.898,56.374c0,0 -62.74,0.465 -62.74,0.465Zm1.623,3.655l64.073,-0.475c0.632,-0.005 1.216,-0.334 1.547,-0.872c0.332,-0.538 0.363,-1.208 0.082,-1.774l-28.371,-57.331c0,-0 120.083,-57.158 120.083,-57.158c0,-0 76.766,27.523 76.766,27.523c0.553,0.198 1.168,0.119 1.653,-0.212c0.486,-0.332 0.782,-0.876 0.799,-1.463l1.915,-69.822c-0,-0 117.765,-57.308 117.765,-57.308c0,0 58.602,5.047 58.602,5.047c10.705,7.796 226.056,164.614 238.524,173.694c0,-0 12.799,58.635 12.799,58.635c0,0 -51.662,97.585 -51.662,97.585c-0,-0 -53.636,-3.807 -53.636,-3.807c-0.579,-0.041 -1.144,0.194 -1.522,0.635c-0.378,0.441 -0.524,1.035 -0.396,1.601l14.698,64.54c-0,0 -53.142,86.669 -53.142,86.669c0,0 -62.021,-5.382 -62.021,-5.382c-0.564,-0.049 -1.118,0.165 -1.503,0.58c-0.385,0.415 -0.556,0.984 -0.465,1.543l10.74,65.561c0,-0 -153.77,210.829 -153.77,210.829c0,0 -59.536,7.683 -59.536,7.683c-0.496,0.064 -0.945,0.327 -1.241,0.73c-0.297,0.403 -0.417,0.909 -0.331,1.402l7.651,43.994c0,-0 -123.891,172.507 -123.891,172.507c0,0 -49.949,-2.69 -49.949,-2.69c0,0 -159.917,-107.913 -159.917,-107.913c0,0 -12.083,-36.951 -12.083,-36.951c-0,0 30.343,-184.848 30.343,-184.848c0,0 40.103,-22.915 40.103,-22.915c0.845,-0.483 1.165,-1.542 0.73,-2.412l-30.881,-61.762c2.026,-15.411 45.444,-345.663 45.444,-345.663l-0,-0Zm22.249,25.359l53.94,-0.456l360.337,239.396l9.974,62.188l-142.669,196.729l-67.802,10.598l8.996,50.765l-119.465,165.545l-38.819,2.089l-146.111,-95.561l-10.976,-38.61l28.386,-171.704l48.222,-32.894l-28.79,-72.685l44.777,-315.4Zm371.693,-193.683l82.239,384.231l19.264,-1.785l28.846,-37.162l-14.83,-109.152l86.554,3.554l29.11,-53.622l-0.336,-32.255l-209.673,-153.449l-21.174,-0.36Zm-281.401,149.339l-2.12,-31.354l49.126,-23.361l124.443,51.328l3.335,-113.725l45.882,-28.771l66.766,334.149l-287.432,-188.266Z\" style=\"fill:#fff;\"/><path d=\"M962.916,592.17c-0.907,0.008 -1.672,0.678 -1.799,1.576l-44.777,315.401c-0.045,0.315 -0.006,0.637 0.111,0.933l28.25,71.321c-0,-0 -47.012,32.067 -47.012,32.067c-0.413,0.282 -0.694,0.722 -0.775,1.216l-28.386,171.704c-0.044,0.266 -0.029,0.54 0.045,0.8l10.977,38.61c0.12,0.425 0.39,0.792 0.76,1.033l146.11,95.562c0.327,0.213 0.713,0.317 1.102,0.296l38.82,-2.089c0.553,-0.03 1.064,-0.309 1.388,-0.758l119.464,-165.546c0.29,-0.402 0.405,-0.904 0.319,-1.392l-8.67,-48.922c0,-0 65.954,-10.309 65.954,-10.309c0.482,-0.076 0.914,-0.34 1.201,-0.735l142.669,-196.73c0.286,-0.394 0.404,-0.886 0.327,-1.367l-9.975,-62.187c-0.081,-0.506 -0.369,-0.954 -0.795,-1.237l-360.337,-239.397c-0.305,-0.202 -0.664,-0.309 -1.03,-0.306l-53.941,0.456Zm1.609,3.654l51.801,-0.438c14.105,9.37 342.391,227.473 359.2,238.641c0,-0 9.727,60.644 9.727,60.644c-0,-0 -141.776,195.497 -141.776,195.497c0,-0 -67.049,10.48 -67.049,10.48c-0.486,0.076 -0.922,0.345 -1.208,0.746c-0.287,0.401 -0.4,0.9 -0.314,1.385l8.861,50.001c0,-0 -118.497,164.205 -118.497,164.205c-0,0 -37.345,2.01 -37.345,2.01c-0,0 -145.036,-94.858 -145.036,-94.858c0,0 -10.676,-37.557 -10.676,-37.557c-0,0 28.19,-170.525 28.19,-170.525c0,0 47.575,-32.451 47.575,-32.451c0.709,-0.483 0.988,-1.393 0.672,-2.19l-28.611,-72.233l44.486,-313.357Z\" style=\"fill:#fff;\"/><path d=\"M1200.47,706.844l-248.519,566.48\" style=\"fill:none;\"/><path d=\"M999.069,1128.02l-49.183,142.298l14.762,9.312l64.402,-133.763l-29.981,-17.847Zm85.597,-247.647l55.377,34.961l91.809,-190.687l-75.991,-50.255l-71.195,205.981Z\" style=\"fill:#fff;\"/><path d=\"M1002.96,859.265l195.997,125.917l-132.327,183.056l-94.661,-56.349l30.991,-252.624Z\" style=\"fill:#fff;\"/></g></g></svg>";
////		String largeicon="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\"><svg width=\"100%\" height=\"100%\" viewBox=\"0 0 2043 1984\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\" xmlns:serif=\"http://www.serif.com/\" style=\"fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;\"><g><path d=\"M1653.88,1420.67l-49.472,-12.368l-1.546,40.196l17.006,6.184l-1.546,-29.374l30.92,9.276l-0,20.098l13.914,-12.368l9.276,-60.294l-13.914,7.73l-4.638,30.92Z\" style=\"fill:#fff;\"/><path d=\"M1597.66,626.377l-49.096,78.289l9.288,15.924l14.597,-22.558l30.519,87.578l19.904,-15.924l-13.269,-41.135l25.212,-27.865l7.961,-41.135l-13.269,-1.327l-6.635,31.846l-19.904,19.904l-14.596,-25.212l19.904,-37.154l-10.616,-21.231Z\" style=\"fill:#fff;\"/><path d=\"M1711.43,761.344l-29.193,59.712l90.232,29.193l-2.654,-41.135l-13.269,-0l-1.327,22.558l-19.904,-7.962l13.269,-50.424l-14.596,-6.634l-11.943,53.077l-18.577,-5.308l13.27,-34.5l-5.308,-18.577Z\" style=\"fill:#fff;\"/><path d=\"M16.989,537.594l51.697,10.552l-54.065,36.185l20.255,31.156l23.527,3.33l-12.82,-25.64l75.193,-57.501l-120.574,-24.621l16.787,26.539Z\" style=\"fill:#fff;\"/><path d=\"M1046.78,267.414c-5.936,-46.865 -43.868,-107.141 -74.023,-137.223l-15.757,9.916c-11.466,65.628 12.54,147.167 39.826,216.991l17.521,-18.423c-21.843,-51.754 -40.762,-112.468 -41.316,-167.279c11.778,3.386 42.589,80.285 51.043,108.606l22.706,-12.588Z\" style=\"fill:#fff;\"/><path d=\"M1109.51,200.572c-7.837,-46.865 -42.131,-123.167 -81.948,-153.249l-20.805,9.916c-15.139,65.628 8.779,175.212 44.808,245.036l12.877,-35.09c-18.585,-54.318 -42.196,-118.205 -36.517,-165.324c15.551,3.386 40.442,82.977 51.604,111.298l29.981,-12.587Z\" style=\"fill:#fff;\"/><path d=\"M1226.62,288.754c3.162,-19.854 11.265,-45.206 21.253,-71.343c25.251,-66.078 61.528,-126.922 61.528,-126.922l54.521,-5.536c-0,-0 14.238,32.611 15.948,67.337c1.158,23.503 -6.203,44.084 -16.943,71.1l-25.57,16.526c21.135,-50.464 24.066,-68.015 3.113,-137.2c-35.042,44.297 -71.389,135.431 -89.41,198.514l-24.44,-12.476Z\" style=\"fill:#fff;\"/><path d=\"M1288.2,306.766c33.167,-57.855 76.45,-93.701 124.697,-97.701c-1.969,33.272 -15.755,57.832 -45.729,86.795l19.068,39.792c25.121,-36.528 46.023,-84.905 47.446,-130.051l-43.302,-15.588c-77.609,-11.676 -98.675,45.043 -128.97,89.631l26.79,27.122Z\" style=\"fill:#fff;\"/><path d=\"M3.329,1136.5l609.292,489.918l-36.418,30.884l48.921,87.359l73.188,-30.898l-11.215,-59.56l757.111,295.365l-0.539,4.128l-27.042,29.501l-694.28,-273.149l4.027,32.827l-106.99,33.885l-75.272,-139.214l16.112,-17.119l-553.085,-445.779l-7.139,-36.7l3.329,-1.448Z\" style=\"fill:#fff;\"/><path d=\"M92.543,1037.98l147.843,-52.905c0,-0 -138.511,52.836 -134.442,55.842c164.72,121.715 480.446,454.789 480.446,454.789l-2.63,12.048l8.543,18.321l77.038,53.415l86.532,58.678l23.591,14.335l25.997,4.676l39.415,-41.685l547.12,116.931l88.332,-137.437c-0,-0 -77.646,145.547 -68.549,148.824l-25.631,1.221l20.042,9.684l-16.179,30.246l-38.787,3.632l32.235,7.753l-29.087,55.245l-682.027,-223.02l12.966,72.611l-49.271,20.746l-41.493,-70.018l41.493,-28.526l-516.059,-451.227l-15.183,-110.073l23.95,17.426l-36.205,-41.532Z\" style=\"fill:#fff;\"/><path d=\"M55.854,1244.36l78.441,60.842c120.71,326.616 435.065,559.744 803.44,559.744c37.665,0 74.764,-2.437 111.196,-7.168l98.374,39.349c-67.35,15.845 -137.49,24.219 -209.57,24.219c-422.207,0 -777.837,-287.317 -881.881,-676.986Zm1590.45,339.319c-43.585,53.633 -93.134,102.23 -147.659,144.795l10.871,-82.625c19.692,-17.684 38.561,-36.268 56.517,-55.662l80.271,-6.508Zm180.587,-369.838c-27.27,123.801 -72.843,218.231 -144.852,317.629l-64.255,-2.67c67.864,-88.568 117.968,-184.732 147.2,-295.712l61.907,-19.247Zm23.479,-198.985c-0.335,50.29 -4.745,99.619 -12.902,147.604l-58.593,7.102c6.532,-34.319 11.013,-69.336 13.314,-104.936l58.181,-49.77Zm-1779.27,-292.861l39.631,64.19c-18.322,68.182 -28.427,139.672 -29.198,213.374l-14.363,5.243l17.579,79.106c0.671,7.698 1.444,15.367 2.321,23.027l-54.883,16.064c-4.69,-37.495 -7.102,-75.621 -7.102,-114.305c-0,-100.137 16.162,-196.529 46.015,-286.699Zm1393.4,-458.482c25.765,18.255 50.537,37.65 74.223,58.128c177.012,153.035 293.311,366.507 309.336,616.17l-55.197,15.464c-13.694,-268.476 -123.916,-476.181 -329.262,-623.551l0.9,-66.211Zm-1198.09,127.165l17.652,65.175c-66.481,78.518 -119.081,169.137 -154.009,268.068l-42.722,-46.943c41.678,-106.633 102.84,-203.533 179.079,-286.3Zm260.93,-197.245l25.832,50.144c-77.7,39.17 -148.631,89.825 -210.593,149.752l-28.216,-50.987c63.241,-59.189 134.897,-109.49 212.977,-148.909Zm168.552,-64.949c77.026,-21.147 158.125,-32.443 241.841,-32.443c2.922,0 5.84,0.014 8.726,0.041l1.344,56.418c-3.364,-0.039 -6.715,-0.059 -10.07,-0.059c-72.305,0 -142.528,8.982 -209.673,25.904l-32.168,-49.861Z\" style=\"fill:#fff;\"/><g><path d=\"M766.287,800.946c9.68,-9.779 18.85,-19.128 27.314,-27.866c76.29,-78.762 170.181,-281.517 170.181,-281.517l-163.631,99.781l-134.859,-235.048l-16.364,-6.294l-39.02,-65.453l-47.831,-79.299l40.279,32.726l-23.916,-80.557l36.503,61.677l-6.293,-98.18l37.761,104.473l7.552,-84.334l16.364,94.404l27.691,28.951l18.881,-10.07l3.776,-62.936l22.657,54.125l-28.95,83.075l125.871,125.342l103.055,-128.115l-44.019,-79.403l113.687,37.792l32.44,-98.455l39.462,-119.088l-36.544,-7.266l10.07,-14.097l47.328,-45.314l108.753,9.063l0.416,0.513l-26.086,20.65l-93.145,13.846l85.593,1.259c0,-0 39.294,64.194 41.811,79.299c2.518,15.104 2.244,122.095 2.244,122.095c-5.804,47.452 -29.47,175.746 -29.47,175.746l32.485,-140.679l6.152,-155.3l-46.655,-79.765l65.338,85.93l-18.933,123.579l0.265,-1.149l23.533,21.432l-14.628,53.043l18.013,-49.96l27.697,25.819l-15.913,22.812l22.604,-17.313c60.76,-14.107 31.281,-40.725 74.321,-7.581l-0.096,-0.263l5.536,10.463l84.209,-48.747l-33.5,101.581c0,-0 63.15,236.925 39.933,463.316l96.799,-73.435l-99.806,99.025c-4.848,36.467 -12.106,72.415 -22.407,106.784l230.03,-142.635l-258.364,216.37c-12.73,26.372 -27.934,51.094 -45.99,73.551l675.942,-213.602l-626.44,336.425l483.425,-60.304l-621.867,188.665l317.519,68.044l-554.634,62.956l747.653,68.699l-999.14,102.999l-99.632,-65.686l-0.157,0.774l-24.584,0.593l-9.246,-23.285l-67.411,-44.58l-7.886,-25.627l-554.69,-878.568l488.042,551.171l-282.354,-972.38l401.045,724.841l-100.299,-513.417l234.53,383.879Zm452.598,144.375l114.359,-117.941l-0.183,3.797l-26.397,26.867l14.82,2.887l14.442,-5.514l24.899,-25.425l10.325,-10.309l-2.833,-16.204l0.929,-2.089l4.835,18.804l-11.527,12.103l-20.601,21.875l8.974,-0.38l10.902,-5.093l17.408,-22.71l5.67,-5.82l0.213,-16.051l0.925,-3.695l1.349,6.458l-0.395,14.077l-3.696,4.999l-17.192,21.243l9.024,-4.314l17.966,-22.116l9.005,-6.561l-1.513,-10.73l4.574,-38.865l-2.767,37.953l1.031,12.442l-9.354,7.367l-15.961,20.296l7.492,-0l14.638,-19.139l9.569,-12.561l-1.723,-9.132l14.735,-69.842l-11.582,-19.026l0.458,-3.892l-2.505,0.529l-0.017,-0.028l-0.002,0.032l-40.326,8.515l1.793,8.582l-5.354,0.175l-26.369,18.958l-36.727,24.845l-11.209,37.721l9.32,2.563l22.478,-18.444l4.459,4.905l-143.985,122.581l-178.779,-126.992l-13.547,-0.249l-344.892,149.006l-58.15,566.079l4.302,13.876l200.17,129.833l13.067,-3.891l329.337,-390.054l126.63,-268.491l-5.654,-17.629l-36.858,-26.181Zm-323.698,-130.718c31.693,-30.311 68.824,-54.744 93.992,-96.785c64.852,-108.331 114.347,-183.414 105.115,-441.723l-5.75,-14.824c5.472,241.718 -40.614,324.195 -112.075,453.088c-23.557,42.489 -47.331,66.113 -81.282,100.244c-4.003,3.827 -7.917,7.748 -11.71,11.81c4.037,-4.093 7.935,-8.015 11.71,-11.81Zm296.365,-690.021c-11.615,15.862 -21.928,15.454 -30.999,-0.477l-20.983,-2.862c-13.354,15.42 -24.799,14.307 -34.337,-3.338l0.953,15.261l18.6,24.322l8.107,68.674l36.245,0.954l5.246,-72.013l23.845,-20.983l-6.677,-9.538Z\" style=\"fill:#fff;\"/><path d=\"M615.235,1524.15l-9.12,-27.89c-0.092,-0.279 -0.114,-0.577 -0.067,-0.867l24.71,-150.527c0.089,-0.545 0.42,-1.021 0.899,-1.295c0,0 31.536,-18.02 31.536,-18.02l-24.399,-48.798c-0.164,-0.327 -0.226,-0.696 -0.178,-1.058l36.975,-281.244c0.119,-0.907 0.889,-1.588 1.804,-1.595c-0,0 50.173,-0.371 50.173,-0.371l-22.31,-45.084c-0.218,-0.44 -0.25,-0.949 -0.089,-1.413c0.161,-0.464 0.501,-0.845 0.944,-1.056l99.019,-47.131c0.441,-0.21 0.947,-0.236 1.407,-0.071c-0,0 60.273,21.611 60.273,21.611l1.504,-54.83c0.019,-0.683 0.416,-1.299 1.031,-1.598l96.405,-46.913c0.298,-0.145 0.63,-0.207 0.96,-0.178l48.207,4.151c0.333,0.029 0.651,0.148 0.921,0.345l193.668,141.028c0.362,0.265 0.616,0.653 0.712,1.092l10.585,48.495c0.092,0.423 0.032,0.866 -0.171,1.249l-42.482,80.243c-0.339,0.641 -1.025,1.022 -1.75,0.971c0,-0 -41.868,-2.972 -41.868,-2.972l11.47,50.368c0.106,0.467 0.026,0.957 -0.225,1.365l-43.766,71.379c-0.363,0.593 -1.03,0.929 -1.722,0.869c0,-0 -48.716,-4.228 -48.716,-4.228l8.404,51.3c0.079,0.484 -0.039,0.98 -0.328,1.376l-125.085,171.5c-0.297,0.407 -0.747,0.674 -1.247,0.738c0,0 -46.875,6.048 -46.875,6.048l5.965,34.301c0.085,0.485 -0.03,0.984 -0.317,1.384l-101.018,140.659c-0.365,0.508 -0.964,0.795 -1.588,0.762l-30.17,-1.625l42.508,27.281l317.825,-394.921l135.312,-264.33l-258.036,-188.807l-339.853,154.192l-66.083,558.555l24.246,15.56Zm53.378,34.258l-49.167,-33.179c-0,0 -9.707,-29.683 -9.707,-29.683c-0,-0 22.497,-137.052 24.496,-149.225c-0,0 32.283,-18.447 32.283,-18.447c0.844,-0.483 1.165,-1.541 0.73,-2.412l-24.923,-49.846c1.812,-13.789 36.693,-279.105 36.693,-279.105l51.506,-0.381c0.632,-0.005 1.216,-0.335 1.548,-0.872c0.331,-0.538 0.362,-1.209 0.082,-1.775l-22.784,-46.04c-0,0 96.649,-46.004 96.649,-46.004c0,0 61.938,22.207 61.938,22.207c0.553,0.198 1.168,0.12 1.653,-0.212c0.485,-0.332 0.782,-0.876 0.798,-1.463l1.543,-56.248c0,0 94.95,-46.205 94.95,-46.205c0,0 47.194,4.065 47.194,4.065c9.548,6.953 181.574,132.222 192.691,140.317c-0,0 10.294,47.159 10.294,47.159c-0,0 -41.609,78.595 -41.609,78.595c-0,-0 -43.144,-3.063 -43.144,-3.063c-0.579,-0.041 -1.143,0.195 -1.521,0.636c-0.378,0.44 -0.525,1.034 -0.396,1.6l11.853,52.05c-0,0 -42.784,69.777 -42.784,69.777c0,-0 -49.936,-4.334 -49.936,-4.334c-0.564,-0.049 -1.118,0.165 -1.503,0.58c-0.385,0.415 -0.556,0.984 -0.465,1.543l8.661,52.869c0,0 -124.168,170.243 -124.168,170.243c0,0 -47.993,6.193 -47.993,6.193c-0.496,0.064 -0.944,0.328 -1.241,0.731c-0.297,0.402 -0.416,0.909 -0.331,1.401l6.162,35.43c0,0 -99.984,139.219 -99.984,139.219l-35.611,-1.918l-84.457,-54.203Zm28.088,-541.739l43.618,-0.369l291.38,193.583l8.065,50.287l-115.367,159.082l-54.827,8.569l7.275,41.05l-96.603,133.865l-31.391,1.689l-118.149,-77.273l-8.876,-31.222l22.954,-138.845l38.994,-26.599l-23.28,-58.775l36.207,-255.042Zm278.731,-181.053l-4.144,4.836l77.646,350.749l33.536,2.412l37.776,-47.744l-8.677,-74.038c-0,-0 62.06,4.104 62.06,4.104l34.807,-64.702l-8.126,-37.568l-185.155,-135.499l-39.723,-2.55Zm4.78,8.29l32.114,2.061c13.377,9.79 165.636,121.216 180.837,132.34c-0,0 6.865,31.739 6.865,31.739c-0,-0 -30.987,57.602 -30.987,57.602c-0,-0 -61.723,-4.082 -61.723,-4.082l-4.219,4.438l8.85,75.512c-0,0 -33.112,41.848 -33.112,41.848c0,0 -23.408,-1.683 -23.408,-1.683l-75.217,-339.775Zm-247.231,108.724l9.875,32.285l284.055,190.4l6.111,-4.15l-71.633,-331.383l-5.779,-2.667l-66.406,35.681l0,74.934c0,-0 -81.576,-31.939 -81.576,-31.939l-74.647,36.839Zm264.283,-92.579l66.5,310.7l15.578,-1.443l23.326,-30.05l-11.992,-88.263l69.99,2.873l23.539,-43.36l-0.271,-26.082l-169.549,-124.084l-17.121,-0.291Zm-247.619,119.824l-7.065,-23.098c-0,-0 65.419,-32.285 65.419,-32.285c0,-0 83.736,32.784 83.736,32.784l5.436,-3.709l-0,-76.008c-0,-0 57.549,-30.922 57.549,-30.922c8.086,37.409 57.615,266.534 68.43,316.567l-273.505,-183.329Zm20.069,0.936l-1.714,-25.353l39.725,-18.891l100.628,41.506l2.697,-91.962l37.102,-23.265l53.989,270.203l-232.427,-152.238Z\" style=\"fill:#fff;\"/><path d=\"M888.779,1107.91l-200.961,458.073\" style=\"fill:none;\"/><path d=\"M730.333,1451.11l-44.345,119.326l8.822,5.563l54.18,-113.783l-18.657,-11.106Zm73.384,-197.466l34.283,21.645l72.595,-152.456l-46.871,-30.658l-60.007,161.469Z\" style=\"fill:#fff;\"/><path d=\"M722.057,1227l165.503,105.983l-94.905,147.295l-103.398,-64.72l32.8,-188.558Z\" style=\"fill:#fff;\"/></g><path d=\"M1349.67,733.838l52.091,-16.166l20.657,133.82l29.638,-148.19l-67.359,-24.249l-51.193,16.166l16.166,38.619Z\" style=\"fill:#fff;\"/><path d=\"M512.604,328.909l-40.431,71.531l38.357,-2.074l-20.733,60.127l15.55,-3.11l20.733,-73.603l-35.247,7.256l30.064,-46.65l29.027,55.98l7.256,-8.293l-36.283,-80.86l-16.587,8.293l8.294,11.403Z\" style=\"fill:#fff;\"/></g></svg>";
//		List<Image> icons=new ArrayList<>();
//		icons.addAll(0,ImageUtilities.getImagesSVG(smallicon, ImageUtilities.MAIN_ICON_COLOR, new int[] {512,256,192,128,96,96,64,48,32,24,16}));
//		//icons.addAll(0,ImageUtilities.getImagesSVG(medicon, ImageUtilities.MAIN_ICON_COLOR, new int[] {96,64,48}));
//		//icons.addAll(0,ImageUtilities.getImagesSVG(largeicon, ImageUtilities.MAIN_ICON_COLOR, new int[] {512,256,192,128,96}));
		setIconImages(ImageUtilities.getApplicationIcons());

		
		build();

		setVisible(true);
	}

	private void setSelectedProject(Project project) {
		this.selectedProject = project;
		openAction.setEnabled(project != null);
	}

	private JComponent buildList() {
		ProjectListModel projectListModel = new ProjectListModel();

		projectList = new JList<>(projectListModel);
		projectList.setCellRenderer(new DynamicListCellRenderer());
		projectList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		projectList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				setSelectedProject(projectList.getSelectedValue());
			}
		});

		// Make the list respond to enter key
		KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true);
		InputMap im = projectList.getInputMap();
		im.put(keyStroke, keyStroke);
		projectList.getActionMap().put(keyStroke, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedProject != null) {
					openAction.actionPerformed(e);
				}
			}
		});
		projectList.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					if (selectedProject != null) {
						openAction.actionPerformed(null);
					}
				}
			}
		});

		JScrollPane jScrollPane = new JScrollPane(projectList);
		jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		return jScrollPane;
	}

	private JComponent buildButtons() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(200, 200));

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 2;
		gbc.insets = new Insets(0, 20, 0, 20);
		panel.add(new JLabel(""), gbc);

		gbc.weighty = 0;
		gbc.gridy++;
		FlatButton newButton = new FlatButton();
		newButton.setAction(new NewAction());
		newButton.setButtonType(ButtonType.roundRect);
		panel.add(newButton, gbc);

		gbc.gridy++;
		gbc.weighty = 1;
		panel.add(new JLabel(""), gbc);

		gbc.gridy++;
		gbc.weighty = 0;

		FlatButton openFolderButton = new FlatButton();
		openFolderButton.setAction(new OpenExisting());
		openFolderButton.setButtonType(ButtonType.roundRect);
		panel.add(openFolderButton, gbc);
		
		gbc.gridy++;
		gbc.weighty = 1;
		panel.add(new JLabel(""), gbc);

		gbc.gridy++;
		gbc.weighty = 0;

		FlatButton openButton = new FlatButton();
		openAction = new OpenAction();
		openButton.setAction(openAction);
		openButton.setButtonType(ButtonType.roundRect);
		panel.add(openButton, gbc);

		gbc.weighty = 20;
		gbc.gridy++;
		panel.add(new JLabel(""), gbc);

		gbc.weighty = 0;
		gbc.gridy++;
		FlatButton closeButton = new FlatButton();
		closeButton.setAction(new CloseAction());
		closeButton.setButtonType(ButtonType.roundRect);
		panel.add(closeButton, gbc);

		gbc.weighty = 1;
		gbc.gridy++;
		panel.add(new JLabel(""), gbc);

		return panel;

	}

	private JComponent buildHeading() {
		// Box box=new Box(BoxLayout.Y_AXIS);
		JPanel panel = new JPanel(new BorderLayout());

		FlatLabel heading = new FlatLabel();
		heading.setText("Infomancer Forge");
		heading.setLabelType(LabelType.h1);
		heading.setHorizontalAlignment(JLabel.CENTER);

		FlatLabel projects = new FlatLabel();
		projects.setText("Projects");
		projects.setLabelType(LabelType.h2);

		panel.add(heading, BorderLayout.NORTH);
		panel.add(projects, BorderLayout.SOUTH);

		return panel;
	}

	FlatCheckBox analyticsCheckBox;

	private JComponent buildFooter() {
		analyticsCheckBox=new FlatCheckBox();
		analyticsCheckBox.setFocusable(false);
		analyticsCheckBox.setText("Share usage information online.");
		
		if (config.getUseAnalytics()!=null && true==config.getUseAnalytics()) {
			analyticsCheckBox.setSelected(true);
		}
		analyticsCheckBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				config.setUseAnalytics(analyticsCheckBox.isSelected());
				StorageUtilities.saveConfig();
			}
		});
		
		
		FlatLabel footer = new FlatLabel();
		footer.setText(Config.getVersion());
		footer.setLabelType(LabelType.small);
		footer.setHorizontalAlignment(JLabel.RIGHT);
		
		JPanel footerPanel=new JPanel(new BorderLayout());
		footerPanel.add(analyticsCheckBox,BorderLayout.WEST);
		footerPanel.add(footer,BorderLayout.EAST);
		footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		
		return footerPanel;
	}

	private void build() {
		JPanel main = new JPanel(new BorderLayout());

		main.add(buildHeading(), BorderLayout.NORTH);
		main.add(buildList(), BorderLayout.CENTER);
		main.add(buildButtons(), BorderLayout.EAST);
		main.add(buildFooter(), BorderLayout.SOUTH);

		main.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

		getContentPane().add(main);
	}

	public class NewAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private NewAction() {
			putValue(NAME, "New");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.MAP_24, ImageUtilities.BUTTON_ICON_COLOR,
					ImageUtilities.BUTTON_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			NewProjectWizard newProjectWizard = new NewProjectWizard();
			if (newProjectWizard.getProject() != null) {
				try {
					AdventureEditor adventureEditor=new AdventureEditor(newProjectWizard.getProject());
					adventureEditor.getAdventureProjectModel().getAnalyticsController().sendEvent("Project", "Create", null);

					AdventureBuildersToolkitLauncher.this.setVisible(false);
				} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e1) {
					ErrorUtilities.showFatalException(e1);
				}
			}
		}

	}

	public class OpenAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private OpenAction() {
			putValue(NAME, "Open");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.OPEN_24, ImageUtilities.BUTTON_ICON_COLOR,
					ImageUtilities.BUTTON_ICON_SIZE));
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				new AdventureEditor(selectedProject);
				StorageUtilities.setProjectFileLastUsed(selectedProject);
				setVisible(false);
			} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e1) {
				ErrorUtilities.showFatalException(e1);
			}
		}

	}

	public class OpenExisting extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private OpenExisting() {
			putValue(NAME, "Open Folder");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.FOLDER_OPEN_24, ImageUtilities.BUTTON_ICON_COLOR,
					ImageUtilities.BUTTON_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jFileChooser=new JFileChooser(StorageUtilities.getConfig().getLastWorkingPath());
			jFileChooser.setDialogTitle("Pick existing Project folder.");
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (jFileChooser.showDialog((Component)e.getSource(),"Select")==JFileChooser.APPROVE_OPTION) {
				String projectFile=jFileChooser.getSelectedFile().getAbsolutePath()+"/"+StorageUtilities.projectJsonFileName;
				Project project=StorageUtilities.loadProjectIfFound(projectFile);
				if (project!=null) {
					try {
						new AdventureEditor(project);
						StorageUtilities.setProjectFileLastUsed(project);
						setVisible(false);
					} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e1) {
						ErrorUtilities.showSeriousException(e1);
					}
				}
			}
		}
	}

	public class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private CloseAction() {
			putValue(NAME, "Close");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.DISMISS_24, ImageUtilities.BUTTON_ICON_COLOR,
					ImageUtilities.BUTTON_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}

	} 

	public class ProjectListModel extends AbstractListModel<Project> {
		private static final long serialVersionUID = 1L;

		private List<Project> projects = new ArrayList<>();

		ProjectListModel() {
			log.trace("Loading known projects.");

			for (var projectFile : config.getKnownProjects()) {
				Project project = StorageUtilities.loadProjectIfFound(projectFile);
				log.trace("Known project reviewed " + projectFile);
				if (project != null) {
					log.trace("Known project loaded " + project.getPath());
					log.trace("Known project loaded " + project.getPath());
					projects.add(project);
				}
			}
		}

		@Override
		public int getSize() {
			return projects.size();
		}

		@Override
		public Project getElementAt(int index) {
			return projects.get(index);
		}
	}

	static public void configureLAF() {
		FlatMaterialDarkerIJTheme.setup();
		
		// Tables
		UIManager.put("TableHeader.separatorColor", ((Color)UIManager.get("TableHeader.background")).darker());
		UIManager.put("Table.alternateRowColor", ImageUtilities.colorAverage((Color)UIManager.get("Table.background"),(Color)UIManager.get("Table.background")).darker());
		UIManager.put("Table.focusSelectedCellHighlightBorder",
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(ImageUtilities.HIGHLIGHT_COLOR,1),
						BorderFactory.createEmptyBorder(0,4,0,4)
						)
		);
		
	}
	
	static public void main(String[] args) {
		configureLAF();
		
		Config config=StorageUtilities.getConfig();
		if (config.getAcceptedTerms()==null || config.getAcceptedTerms()==false) {
			new TermsFrame();
		} else {
			new AdventureBuildersToolkitLauncher();
		}
	}
}
