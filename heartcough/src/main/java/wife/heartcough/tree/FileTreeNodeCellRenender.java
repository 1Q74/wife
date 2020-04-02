package wife.heartcough.tree;

import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import wife.heartcough.common.FileSystem;




public class FileTreeNodeCellRenender extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;
    private JLabel label;

    public FileTreeNodeCellRenender() {
        label = new JLabel();
        label.setOpaque(true);
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
        File file = (File)node.getUserObject();
        
        label.setIcon(FileSystem.VIEW.getSystemIcon(file));
        label.setText(FileSystem.VIEW.getSystemDisplayName(file));
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