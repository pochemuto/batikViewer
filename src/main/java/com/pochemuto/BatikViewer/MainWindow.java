package com.pochemuto.BatikViewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.swing.svg.SVGUserAgentAdapter;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MainWindow extends JFrame {

    private final JLabel filename;
    private final JSVGCanvas canvas;
    private final LogWindow logWindow;
    private File file;
    private final JButton reloadFile;
    private final JFileChooser fileChooser;
    private final JButton selectFile;
    private final static String TITLE = "Batik SVG Viewer!";
    private final static String TITLE_LOADING = TITLE + " - Loading...";
    private SVGOMDocument document;
    private final JLabel logMessage;

    public MainWindow() {
        setSize(800, 1000);
        setTitle(TITLE);
        URL resource = MainWindow.class.getResource("/icon.png");
        ImageIcon iIcon = new ImageIcon(resource);
        Image img = iIcon.getImage();
        setIconImage(img);

        logWindow = new LogWindow();
        BorderLayout bl = new BorderLayout();

        Container contentPane = getContentPane();
        contentPane.setLayout(bl);

        // -- top panel
        final JPanel controlPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 5, 5);
        controlPanel.setLayout(flowLayout);

        reloadFile = new JButton("Reload");
        reloadFile.setEnabled(false);
        reloadFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                loadSVG(file);
            }
        });
        controlPanel.add(reloadFile);

        selectFile = new JButton("...");
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("SVG", "svg"));
        selectFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                int result = fileChooser.showOpenDialog(MainWindow.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    fileSelected(fileChooser.getSelectedFile());
                }
            }
        });
        controlPanel.add(selectFile);

        filename = new JLabel("<select file>");
        controlPanel.add(filename);

        logMessage = new JLabel(" ");
        logMessage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logWindow.setVisible(true);
            }
        });
        contentPane.add(logMessage, BorderLayout.SOUTH);

        contentPane.add(controlPanel, BorderLayout.NORTH);
        // ------
        canvas = new JSVGCanvas(new SVGUserAgentAdapter() {
            @Override
            public void displayError(final Exception ex) {
                logMessage(ex);
            }

            @Override
            public void displayError(final String message) {
                logMessage(message);
            }
        }, true, true);

        canvas.setDocumentState(JSVGComponent.ALWAYS_DYNAMIC);
        canvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildStarted(final GVTTreeBuilderEvent e) {
                logMessage("==>> loading" + file.getPath() + "...");
                setControlEnabled(false);
            }

        });

        canvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            @Override
            public void gvtRenderingCompleted(final GVTTreeRendererEvent e) {
                ready();
                setControlEnabled(true);
                logMessage("done " + file.getPath());
            }
        });

        contentPane.add(canvas, BorderLayout.CENTER);

        //---
        DropTargetListener dropTargetListener = new FileDropTargetListener() {
            @Override
            void dropFile(final File file) {
                fileSelected(file);
            }
        };

        new DropTarget(this, dropTargetListener);
    }

    public void fileSelected(final File file) {
        reloadFile.setEnabled(true);
        fileChooser.setCurrentDirectory(file);
        this.file = file;
        filename.setText(file.getName());
        loadSVG(file);
    }

    private void loadSVG(final File file) {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        try {

            FileInputStream input = new FileInputStream(file);
            document = (SVGOMDocument) f.createDocument(null, input);
            canvas.setDocument(document);
        } catch (Exception e) {
            logMessage(e);
        }
    }

    private void setControlEnabled(final boolean enabled) {
        reloadFile.setEnabled(enabled);
        selectFile.setEnabled(enabled);
        setTitle(enabled ? TITLE : TITLE_LOADING);
    }

    private void ready() {
        NodeList nodes;
        try {
            nodes = XPathAPI.selectNodeList(document, "//svg:g");
            System.out.println(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                element.setAttribute("cursor", "pointer");
            }
        } catch (Exception e) {
            logMessage(e);
        }

    }

    private void logMessage(Exception e) {
        System.err.println(e);
        logMessage.setText(e.getMessage());
        logWindow.log(e);
    }

    private void logMessage(String e) {
        System.err.println(e);
        logMessage.setText(e);
        logWindow.log(e);
    }
}
