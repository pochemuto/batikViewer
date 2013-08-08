package com.pochemuto.BatikViewer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

public abstract class FileDropTargetListener implements DropTargetListener {

    public void dragEnter(final DropTargetDragEvent event) {
    }

    public void dragOver(final DropTargetDragEvent dtde) {

    }

    public void dropActionChanged(final DropTargetDragEvent dtde) {

    }

    public void dragExit(final DropTargetEvent dte) {

    }

    public void drop(final DropTargetDropEvent event) {
        event.acceptDrop(DnDConstants.ACTION_MOVE);

        Transferable transferable = event.getTransferable();

        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for (DataFlavor flavor : flavors) {
            try {
                if (flavor.isFlavorJavaFileListType()) {
                    List<File> files = (List<File>) transferable.getTransferData(flavor);
                    for (File f : files) {
                        dropFile(f);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    abstract void dropFile(File file);
}
