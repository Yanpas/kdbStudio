package studio.ui;

import org.netbeans.editor.Utilities;
import studio.kdb.*;
import studio.kdb.ListModel;
import studio.qeditor.QKit;
import studio.ui.action.QueryResult;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class TabPanel extends JPanel {
    private JComponent component = null;

    private JToolBar toolbar = null;
    private JToggleButton tglBtnComma;
    private QueryResult queryResult;
    private K.KBase result;
    private JEditorPane textArea = null;
    private QGrid grid = null;
    private KFormatContext formatContext = new KFormatContext(KFormatContext.DEFAULT);
    private ResultType type;

    public TabPanel(QueryResult queryResult) {
        this.queryResult = queryResult;
        this.result = queryResult.getResult();
        initComponents();
    }

    public ResultType getType() {
        return type;
    }

    private void initComponents() {
        if (result != null) {
            KTableModel model = KTableModel.getModel(result);
            if (model != null) {
                grid = new QGrid(model);
                component = grid;
                if (model instanceof DictModel) {
                    type = ResultType.DICT;
                } else if (model instanceof ListModel) {
                    type = ResultType.LIST;
                } else {
                    type = ResultType.TABLE;
                }
            } else {
                textArea = new JEditorPane(QKit.CONTENT_TYPE, "");
                textArea.setEditable(false);
                component = Utilities.getEditorUI(textArea).getExtComponent();
                type = ResultType.TEXT;
            }

            tglBtnComma = new JToggleButton(Util.COMMA_CROSSED_ICON);
            tglBtnComma.setSelectedIcon(Util.COMMA_ICON);

            tglBtnComma.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            tglBtnComma.setToolTipText("Add comma as thousands separators for numbers");
            tglBtnComma.setFocusable(false);
            tglBtnComma.addActionListener(e -> {
                updateFormatting();
            });
            toolbar = new JToolBar();
            toolbar.setFloatable(false);
            toolbar.add(tglBtnComma);
            updateFormatting();
        } else {
            textArea = new JTextPane();
            String hint = QErrors.lookup(queryResult.getError().getMessage());
            hint = hint == null ? "" : "\nStudio Hint: Possibly this error refers to " + hint;
            textArea.setText("An error occurred during execution of the query.\nThe server sent the response:\n" + queryResult.getError().getMessage() + hint);
            textArea.setForeground(Color.RED);
            textArea.setEditable(false);
            component = new JScrollPane(textArea);
            type = ResultType.ERROR;
        }

        setLayout(new BorderLayout());
        add(component, BorderLayout.CENTER);
    }

    public void addInto(JTabbedPane tabbedPane) {
        String title = type.title;
        if (isTable()) {
            title = title + " [" + grid.getRowCount() + " rows] ";
        }
        tabbedPane.addTab(title, type.icon, this);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
        updateToolbarLocation(tabbedPane);
    }

    public void updateToolbarLocation(JTabbedPane tabbedPane) {
        if (toolbar == null) return;

        remove(toolbar);
        if (tabbedPane.getTabPlacement() == JTabbedPane.TOP) {
            toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
            add(toolbar, BorderLayout.WEST);
        } else {
            toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
            add(toolbar, BorderLayout.NORTH);
        }
    }

    private void updateFormatting() {
        formatContext.setShowThousandsComma(tglBtnComma.isSelected());
        if (grid != null) {
            grid.setFormatContext(formatContext);
        }
        if (type == ResultType.TEXT) {
            String text;
            if ((result instanceof K.UnaryPrimitive&&0==((K.UnaryPrimitive)result).getPrimitiveAsInt())) text = "";
            else {
                text = Util.limitString(result.toString(formatContext), Config.getInstance().getMaxCharsInResult());
            }
            textArea.setText(text);
        }
    }

    public void toggleCommaFormatting() {
        if (tglBtnComma == null) return;
        tglBtnComma.doClick();
    }

    public JTable getTable() {
        if (grid == null) return null;
        return grid.getTable();
    }

    public boolean isTable() {
        return grid != null;
    }

    public enum ResultType {
        ERROR("Error Details ", Util.ERROR_SMALL_ICON),
        TEXT(I18n.getString("ConsoleView"), Util.CONSOLE_ICON),
        LIST("List", Util.TABLE_ICON),
        DICT("Dict", Util.TABLE_ICON),
        TABLE("Table", Util.TABLE_ICON);

        private final String title;
        private final Icon icon;
        ResultType(String title, Icon icon) {
            this.title = title;
            this.icon = icon;
        }
    };
}

