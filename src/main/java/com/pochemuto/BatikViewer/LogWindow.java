package com.pochemuto.BatikViewer;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/** @author a.kramarev */
public class LogWindow extends JFrame {

    private final JTextArea textArea;
    private final JScrollPane scrollPane;

    public LogWindow() throws HeadlessException {
        setLayout(new BorderLayout());
        setSize(1000,800);

        textArea = new JTextArea();
        textArea.setFont(new JLabel().getFont());
        textArea.setEditable(false);
        ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scrollPane = new JScrollPane(textArea);
        add(scrollPane);
    }

    public void log(Exception e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        textArea.append(writer.toString());
    }

    public void log(String message) {
        textArea.append(message);
        textArea.append("\n");
    }
}
