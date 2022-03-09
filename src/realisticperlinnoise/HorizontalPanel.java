/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realisticperlinnoise;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author fes77
 */
public class HorizontalPanel extends JPanel {

    public HorizontalPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }

}
