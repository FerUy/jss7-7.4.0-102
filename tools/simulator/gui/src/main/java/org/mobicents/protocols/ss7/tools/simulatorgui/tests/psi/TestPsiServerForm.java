/*
 * TeleStax, Open Source Cloud Communications  Copyright 2012.
 * and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.protocols.ss7.tools.simulatorgui.tests.psi;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.management.Notification;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mobicents.protocols.ss7.tools.simulator.tests.psi.TestPsiServerManMBean;
import org.mobicents.protocols.ss7.tools.simulatorgui.TestingForm;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class TestPsiServerForm extends TestingForm {

  private static final long serialVersionUID = 6864080004816461701L;

  private TestPsiServerManMBean testPsiServerManMBean;

  private JLabel lbMessage;
  private JLabel lbResult;
  private JLabel lbState;

  public TestPsiServerForm(JFrame owner) {
    super(owner);

    JPanel panel = new JPanel();
    panel_c.add(panel, BorderLayout.CENTER);
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[]{0, 0, 0};
    gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
    gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    panel.setLayout(gbl_panel);

    JPanel panel_btn = new JPanel();
    panel_btn.setLayout(null);
    GridBagConstraints gbc_panel_btn = new GridBagConstraints();
    gbc_panel_btn.insets = new Insets(0, 0, 5, 0);
    gbc_panel_btn.fill = GridBagConstraints.BOTH;
    gbc_panel_btn.gridx = 1;
    gbc_panel_btn.gridy = 3;
    panel.add(panel_btn, gbc_panel_btn);

    JLabel label_3 = new JLabel("Operation result");
    GridBagConstraints gbc_label_3 = new GridBagConstraints();
    gbc_label_3.insets = new Insets(0, 0, 5, 5);
    gbc_label_3.gridx = 0;
    gbc_label_3.gridy = 5;
    panel.add(label_3, gbc_label_3);

    lbResult = new JLabel("-");
    GridBagConstraints gbc_lbResult = new GridBagConstraints();
    gbc_lbResult.insets = new Insets(0, 0, 5, 0);
    gbc_lbResult.gridx = 1;
    gbc_lbResult.gridy = 5;
    panel.add(lbResult, gbc_lbResult);

    JLabel label_4 = new JLabel("Message received");
    GridBagConstraints gbc_label_4 = new GridBagConstraints();
    gbc_label_4.insets = new Insets(0, 0, 5, 5);
    gbc_label_4.gridx = 0;
    gbc_label_4.gridy = 6;
    panel.add(label_4, gbc_label_4);

    lbMessage = new JLabel("-");
    GridBagConstraints gbc_lbMessage = new GridBagConstraints();
    gbc_lbMessage.insets = new Insets(0, 0, 5, 0);
    gbc_lbMessage.gridx = 1;
    gbc_lbMessage.gridy = 6;
    panel.add(lbMessage, gbc_lbMessage);

    lbState = new JLabel("-");
    GridBagConstraints gbc_lbState = new GridBagConstraints();
    gbc_lbState.gridx = 1;
    gbc_lbState.gridy = 7;
    panel.add(lbState, gbc_lbState);
  }

  public void setData(TestPsiServerManMBean testPsiServerManMBean) {
    this.testPsiServerManMBean = testPsiServerManMBean;
  }

  private void closeCurrentDialog() {
    this.lbMessage.setText("");
        /* String res = this.mapPsiServer.closeCurrentDialog();
        this.lbResult.setText(res); */
  }

  @Override
  public void sendNotif(Notification notification) {
    super.sendNotif(notification);

    String msg = notification.getMessage();
    final String[] prefixes = new String[]{"Rcvd: PSIReq: ", "Sent: PSIResp: "};
    if (msg != null) {
      for (String prefix : prefixes) {
        if (msg.startsWith(prefix)) {
          String s1 = msg.substring(prefix.length());
          this.lbMessage.setText(s1);
          return;
        }
      }
    }
  }

  @Override
  public void refreshState() {
    super.refreshState();

    String s1 = this.testPsiServerManMBean.getCurrentRequestDef();
    this.lbState.setText(s1);
  }

}
