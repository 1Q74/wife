package wife.heartcough.tree;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import wife.heartcough.Explorer;
import wife.heartcough.Synchronizer;
import wife.heartcough.path.DirectoryPath;




public class FileTreeListener {
	
	public static MouseListener getMouseListener() {
		return 
			new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Synchronizer.restorePath();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
	}
	
	public static FocusListener getFocusListener() {
		return
			new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
//					explorer.refresh();
				}

				@Override
				public void focusLost(FocusEvent e) {
					// TODO Auto-generated method stub
					
				}
		};
	}
	
}