package wife.heartcough.path;

import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * JTextField에 아이콘을 넣을 수 있도록 확장합니다.
 * 
 * -------------------------------------------------------------------
 * 다음 URL을 참고해서 만들었습니다.
 *   https://stackoverflow.com/questions/6089410/decorating-a-jtextfield-with-an-image-and-hint
 *   [mneri], [Eli Sadoff]님께 감사의 마음을 전합니다.
 * -------------------------------------------------------------------
 *  
 * @author jdk
 */
public class IconTextField extends JTextField {
 
	private static final long serialVersionUID = 1L;
	
	private IconTextComponentHelper mHelper = new IconTextComponentHelper(this);

    public IconTextField() {
        super();
    }

    public IconTextField(int cols) {
        super(cols);
    }

    private IconTextComponentHelper getHelper() {
        if (mHelper == null)
            mHelper = new IconTextComponentHelper(this);

        return mHelper;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        getHelper().onPaintComponent(graphics);
    }

    public void setIcon(Icon icon) {
        getHelper().onSetIcon(icon);
    }

    @Override
    public void setBorder(Border border) {
        getHelper().onSetBorder(border);
        super.setBorder(getHelper().getBorder());
    }
    
}