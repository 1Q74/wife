package wife.heartcough.path;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

/**
 * -------------------------------------------------------------------
 * 다음 URL을 참고해서 만들었습니다.
 *   https://stackoverflow.com/questions/6089410/decorating-a-jtextfield-with-an-image-and-hint
 *   [mneri], [Eli Sadoff]님께 감사의 마음을 전합니다.
 * -------------------------------------------------------------------
 * @author jdk
 */
public class IconTextComponentHelper {
    private static final int ICON_SPACING = 4;

    private Border mBorder;
    private Icon mIcon;
    private Border mOrigBorder;
    private JTextComponent mTextComponent;

    public IconTextComponentHelper(JTextComponent component) {
        mTextComponent = component;
        mOrigBorder = component.getBorder();
        mBorder = mOrigBorder;
    }

    public Border getBorder() {
        return mBorder;
    }

    public void onPaintComponent(Graphics g) {
        if (mIcon != null) {
            Insets iconInsets = mOrigBorder.getBorderInsets(mTextComponent);
            mIcon.paintIcon(mTextComponent, g, iconInsets.left, iconInsets.top);
        }
    }

    public void onSetBorder(Border border) {
        mOrigBorder = border;

        if (mIcon == null) {
            mBorder = border;
        } else {
            Border margin = BorderFactory.createEmptyBorder(0, mIcon.getIconWidth() + ICON_SPACING, 0, 0);
            mBorder = BorderFactory.createCompoundBorder(border, margin);
        }
    }

    public void onSetIcon(Icon icon) {
        mIcon = icon;
        resetBorder();
    }
    
    private void resetBorder() {
        mTextComponent.setBorder(mOrigBorder);
    }
}
