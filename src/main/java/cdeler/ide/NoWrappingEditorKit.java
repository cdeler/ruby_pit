package cdeler.ide;

import javax.swing.text.AbstractDocument;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

// https://stackoverflow.com/questions/23149512/how-to-disable-wordwrap-in-java-jtextpane
public class NoWrappingEditorKit extends StyledEditorKit {
    private static final ViewFactory styledEditorKitFactory = (new StyledEditorKit()).getViewFactory();

    private static final ViewFactory defaultFactory = new ExtendedStyledViewFactory();

    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

    /* The extended view factory */
    private static class ExtendedStyledViewFactory implements ViewFactory {
        public View create(Element elem) {
            String elementName = elem.getName();
            if (elementName != null) {
                if (elementName.equals(AbstractDocument.ParagraphElementName)) {
                    return new ExtendedParagraphView(elem);
                }
            }

            // Delegate others to StyledEditorKit
            return styledEditorKitFactory.create(elem);
        }
    }

    private static class ExtendedParagraphView extends ParagraphView {
        public ExtendedParagraphView(Element elem) {
            super(elem);
        }

        @Override
        public float getMinimumSpan(int axis) {
            return super.getPreferredSpan(axis);
        }

    }
}
