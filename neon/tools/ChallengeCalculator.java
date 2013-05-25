package neon.tools;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import neon.util.Dice;

public class ChallengeCalculator {
	private JLabel uitkomst;
	private JFrame window;
	private JTextField hpField1 = new JTextField(10);
	private JTextField hpField2 = new JTextField(10);
	private JTextField spdField1 = new JTextField(10);
	private JTextField spdField2 = new JTextField(10);
	private JTextField avField1 = new JTextField(10);
	private JTextField avField2 = new JTextField(10);
	
	public ChallengeCalculator() {
		window = new JFrame("Calculator");
				
		uitkomst = new JLabel();
		JButton calc = new JButton("calculate");
		calc.addActionListener(new Calculator());
		
		JPanel one = new JPanel();
		one.add(new JLabel("hp:"));
		one.add(hpField1);
		one.add(new JLabel("spd:"));
		one.add(spdField1);
		one.add(new JLabel("av:"));
		one.add(avField1);
		JPanel two = new JPanel();
		two.add(new JLabel("hp:"));
		two.add(hpField2);
		two.add(new JLabel("spd:"));
		two.add(spdField2);
		two.add(new JLabel("av:"));
		two.add(avField2);
		
		JPanel content = new JPanel(new GridLayout(0,1));
		content.add(one);
		content.add(two);
		content.add(calc);
		content.add(uitkomst);
		window.setContentPane(content);		
	}
	
	public void show() {
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	private class Calculator implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			int spd1 = Integer.parseInt(spdField1.getText());
			int hp1 = Dice.roll(hpField1.getText());
			int hp2 = Dice.roll(hpField2.getText());
			while(hp2 > 0) {
				hp2 -= Dice.roll(avField1.getText());
				System.out.println(hp2);
				int spd2 = Integer.parseInt(spdField1.getText());
				while(spd2 > spd1*Math.random()) {
					spd2 -= spd1;
					hp1-= Dice.roll(avField2.getText());
				}
			}
			uitkomst.setText("hp1 = " + hp1);
		}
	}
}
