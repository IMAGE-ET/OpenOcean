/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.order;

/**
 *
 * @author kazushi
 */
public interface IDiseaseView {
    
    /**
     * @return the clearBtn
     */
    public javax.swing.JButton getClearBtn();

    /**
     * @return the countField
     */
    public javax.swing.JTextField getCountField();

    /**
     * @return the deleteBtn
     */
    public javax.swing.JButton getDeleteBtn();

    /**
     * @return the infoLabel
     */
    public javax.swing.JLabel getInfoLabel();


    /**
     * @return the okBtn
     */
    public javax.swing.JButton getOkBtn();

    /**
     * @return the okCntBtn
     */
    public javax.swing.JButton getOkCntBtn();

    /**
     * @return the rtBtn
     */
    public javax.swing.JCheckBox getRtBtn();

    /**
     * @return the searchResultTabel
     */
    public javax.swing.JTable getSearchResultTable();

    /**
     * @return the searchTextField
     */
    public javax.swing.JTextField getSearchTextField();

    /**
     * @return the stampNameField
     */
    public javax.swing.JTextField getStampNameField();

    /**
     * @return the techChk
     */
    public javax.swing.JCheckBox getDiseaseCheck();

    /**
     * @return the setTable
     */
    public javax.swing.JTable getSetTable();

    public javax.swing.JCheckBox getPartialChk();
    
//s.oh^ 2014/10/22 Icon表示
    public javax.swing.JLabel getSearchLabel();
//s.oh$
    
}
