package open.dolphin.order;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.table.ListTableModel;


/**
 * RegisteredDiagnosisTransferHandler
 *
 * @author Minagawa,Kazushi
 *
 */
public final class RegisteredDiagnosisTransferHandler extends TransferHandler {
    
    private final DataFlavor registeredDiagnosisFlavor = RegisteredDiagnosisTransferable.registeredDiagnosisFlavor;
    
    private JTable sourceTable;
    private boolean shouldRemove;
    private RegisteredDiagnosisModel dragItem;
    
    public RegisteredDiagnosisTransferHandler() {
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        sourceTable = (JTable) c;
        ListTableModel<RegisteredDiagnosisModel> tableModel = (ListTableModel<RegisteredDiagnosisModel>) sourceTable.getModel();
        int fromIndex = sourceTable.getSelectedRow();
        dragItem = tableModel.getObject(fromIndex);
        return dragItem != null ? new RegisteredDiagnosisTransferable(dragItem) : null;
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }
    
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {

        if (!canImport(support)) {
            return false;
        }

        try {
            JTable.DropLocation dl = (JTable.DropLocation)support.getDropLocation();
            int toIndex = dl.getRow();

            if (dl.isInsertRow() && toIndex>-1) {
                Transferable t = support.getTransferable();
                RegisteredDiagnosisModel dropItem = (RegisteredDiagnosisModel) t.getTransferData(registeredDiagnosisFlavor);
                JTable dropTable = (JTable) support.getComponent();
                ListTableModel<RegisteredDiagnosisModel> tableModel = (ListTableModel<RegisteredDiagnosisModel>) dropTable.getModel();
                shouldRemove = dropTable == sourceTable;

                if (toIndex<tableModel.getObjectCount()) {
                    tableModel.addObject(toIndex, dropItem);
                } else {
                    tableModel.addObject(dropItem);
                }
                
                return true;
            }

        } catch (UnsupportedFlavorException | IOException ioe) {
            ioe.printStackTrace(System.err);
        }
        
        return false;
    }
    
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action == MOVE && shouldRemove && dragItem!=null) {
            ListTableModel<RegisteredDiagnosisModel> tableModel = (ListTableModel<RegisteredDiagnosisModel>)sourceTable.getModel();
            List<RegisteredDiagnosisModel> list = tableModel.getDataProvider();
            for (int i = 0; i < list.size(); i++) {
                RegisteredDiagnosisModel r = list.get(i);
                if (r==dragItem) {
                    list.remove(i);
                    tableModel.fireTableDataChanged();
                    break;
                }
            }
        }
        shouldRemove = false;
        dragItem = null;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return (support.isDrop() && support.isDataFlavorSupported(registeredDiagnosisFlavor));
    }
}
