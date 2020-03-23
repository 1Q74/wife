package wife.heartcough.tree;

import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CellRenender extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private FileSystemView fileSystemView;
    private JLabel label;

    public CellRenender() {
        label = new JLabel();
        label.setOpaque(true);
        fileSystemView = FileSystemView.getFileSystemView();
    }

    @Override
    public Component getTreeCellRendererComponent(	JTree tree
    												, Object value
											        , boolean selected
											        , boolean expanded
											        , boolean leaf
											        , int row
											        , boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
//        File file = (File)node.getUserObject();
        Object userObject = node.getUserObject();
        File file = null;
        if(userObject instanceof File) {
        	file = (File)userObject;
        } else {
        	file = new File((String)userObject);
        }
        System.out.println(file);
        
        
        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(fileSystemView.getSystemDisplayName(file));
        label.setToolTipText(file.getPath());

        if(selected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
    }
    
}