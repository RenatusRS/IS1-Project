package etf.korisnickamaven;

import entiteti.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;



public class KorisnickaAplikacija extends Frame {

	Label labelInfo;

	private KorisnickaAplikacija() {
		setLocation(250, 250);
		setTitle("IS1 Projekat - Banka");

		Panel panelTop = new Panel(new BorderLayout());
		Panel panelTopCenter = new Panel(new GridLayout(2, 1));

		panelTopCenter.add(panelCreate());
		panelTopCenter.add(panelTransaction());

		panelTop.add(panelTopCenter, BorderLayout.CENTER);
		panelTop.add(panelRacun(), BorderLayout.EAST);

		add(panelTop, BorderLayout.NORTH);
		add(panelGet(), BorderLayout.CENTER);
		add(labelInfo = new Label(), BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		pack();
		setVisible(true);
	}

	private JPanel panelCreate() {
		JPanel ret = new JPanel(new GridLayout(2, 5));
		ret.setBorder(BorderFactory.createTitledBorder("Napraviti"));

		String[] opcije = {"Mesto", "Filijala", "Komitent", "Komitent izmena"};
		JComboBox<String> cb = new JComboBox<>(opcije);

		TextField textNaziv = new TextField();

		TextField textRandom = new TextField();

		TextField textKonekcija = new TextField();
		textKonekcija.setEnabled(false);

		Button buttonNapravi = new Button("Napravi");

		Label labelNaziv = new Label("Naziv");
		Label labelRandom = new Label("Postanski broj");
		Label labelKonekcija = new Label("/");

		ret.add(new Label("Tip"));
		ret.add(labelNaziv);
		ret.add(labelRandom);
		ret.add(labelKonekcija);
		ret.add(new Label());

		ret.add(cb);
		ret.add(textNaziv);
		ret.add(textRandom);
		ret.add(textKonekcija);
		ret.add(buttonNapravi);

		cb.addActionListener(e -> {
			switch ((String) cb.getSelectedItem()) {
				case ("Mesto"):
					labelNaziv.setText("Naziv");
					labelRandom.setText("Postanski broj");
					labelKonekcija.setText("/");
					textKonekcija.setEnabled(false);
					break;
				case ("Komitent"):
				case ("Filijala"):
					labelNaziv.setText("Naziv");
					labelRandom.setText("Adresa");
					labelKonekcija.setText("Mesto");
					textKonekcija.setEnabled(true);
					break;
				case ("Komitent izmena"):
					labelNaziv.setText("Id Komitenta");
					labelRandom.setText("Mesto");
					labelKonekcija.setText("/");
					textKonekcija.setEnabled(false);
					break;
			}
		});

		buttonNapravi.addActionListener(e -> {
			try {
				if (textNaziv.getText().isEmpty() || textRandom.getText().isEmpty() || textKonekcija.isEnabled() && textKonekcija.getText().isEmpty())
					throw new Exception("Niste popunili sva polja!");

				switch ((String) cb.getSelectedItem()) {
					case ("Mesto"):
						labelInfo.setText((String) Connection.recieve("CreateMesto",textNaziv.getText(),textRandom.getText()));
						labelInfo.setForeground(Color.blue);
						break;
					case ("Komitent"):
					case ("Filijala"):
						break;
					case ("Komitent izmena"):
						break;
				}

			} catch (Exception ex) {
				labelInfo.setForeground(Color.red);
				labelInfo.setText(ex.getMessage());
			}
		});

		return ret;
	}

	private JPanel panelTransaction() {
		JPanel ret = new JPanel(new BorderLayout());
		ret.setBorder(BorderFactory.createTitledBorder("Transakcije"));

		JRadioButton radioUplata = new JRadioButton("Promena", true);
		JRadioButton radioPrenos = new JRadioButton("Prenos");

		ButtonGroup bg = new ButtonGroup();
		bg.add(radioUplata);
		bg.add(radioPrenos);

		Panel radioPanel = new Panel(new GridLayout(3, 1));

		radioPanel.add(radioUplata);
		radioPanel.add(radioPrenos);

		Panel inputPanel = new Panel(new GridLayout(2, 3));

		TextField textRacun1 = new TextField();
		TextField textIznos = new TextField();
		TextField textRacun2 = new TextField();

		Button buttonPotvrdi = new Button("Potvrdi");

		radioUplata.addActionListener(e -> textRacun2.setEnabled(false));
		radioPrenos.addActionListener(e -> textRacun2.setEnabled(true));

		inputPanel.add(new Label("Racun 1"));
		inputPanel.add(new Label("Iznos"));
		inputPanel.add(new Label("Racun 2"));
		inputPanel.add(new Label());

		inputPanel.add(textRacun1);
		inputPanel.add(textIznos);
		inputPanel.add(textRacun2);
		inputPanel.add(buttonPotvrdi);

		textRacun2.setEnabled(false);

		ret.add(radioPanel, BorderLayout.WEST);
		ret.add(inputPanel, BorderLayout.CENTER);

		buttonPotvrdi.addActionListener(e -> {
			try {
				if (textRacun1.getText().isEmpty() || textIznos.getText().isEmpty() || textRacun2.isEnabled() && textRacun2.getText().isEmpty())
					throw new Exception("Niste popunili sva polja!");
			} catch (Exception ex) {
				labelInfo.setForeground(Color.red);
				labelInfo.setText(ex.getMessage());
			}
		});

		return ret;
	}

	private JPanel panelRacun() {
		JPanel ret = new JPanel(new BorderLayout());
		ret.setBorder(BorderFactory.createTitledBorder("Racun"));

		JRadioButton radioOpen = new JRadioButton("Otvori", true);
		JRadioButton radioClose = new JRadioButton("Zatvori");

		ButtonGroup bg = new ButtonGroup();
		bg.add(radioOpen);
		bg.add(radioClose);

		Label labelRandom = new Label("ID Komitenta");
		Label labelFilijala = new Label("ID Filijale");
		Label labelStanje = new Label("Stanje");
		Label labelMinus = new Label("Dozvoljeni minus");

		TextField textRandom = new TextField();
		TextField textFilijala = new TextField();
		TextField textStanje = new TextField();
		TextField textMinus = new TextField();

		Panel panelCenter = new Panel(new GridLayout(5, 2));
		panelCenter.add(radioOpen);
		panelCenter.add(radioClose);
		panelCenter.add(labelRandom);
		panelCenter.add(labelFilijala);
		panelCenter.add(textRandom);
		panelCenter.add(textFilijala);
		panelCenter.add(labelStanje);
		panelCenter.add(labelMinus);
		panelCenter.add(textStanje);
		panelCenter.add(textMinus);

		Button buttonAccept = new Button("Primeni");

		ret.add(panelCenter, BorderLayout.CENTER);
		ret.add(buttonAccept, BorderLayout.SOUTH);

		radioOpen.addActionListener(e -> {
			labelRandom.setText("ID Komitenta");
			labelFilijala.setText("ID Filijale");
			labelStanje.setText("Stanje");
			labelMinus.setText("Dozvoljeni minus");

			textFilijala.setEnabled(true);
			textStanje.setEnabled(true);
			textMinus.setEnabled(true);
		});

		radioClose.addActionListener(e -> {
			labelRandom.setText("ID Racuna");
			labelFilijala.setText("/");
			labelStanje.setText("/");
			labelMinus.setText("/");

			textFilijala.setEnabled(false);
			textStanje.setEnabled(false);
			textMinus.setEnabled(false);
		});

		buttonAccept.addActionListener(e -> {
			try {
				if (textRandom.getText().isEmpty() || (textFilijala.isEnabled() && textFilijala.getText().isEmpty() || textStanje.getText().isEmpty() || textMinus.getText().isEmpty()))
					throw new Exception("Niste popunili sva polja!");
			} catch (Exception ex) {
				labelInfo.setForeground(Color.red);
				labelInfo.setText(ex.getMessage());
			}
		});

		return ret;
	}

	private JPanel panelGet() {
		JPanel ret = new JPanel(new BorderLayout());
		ret.setBorder(BorderFactory.createTitledBorder("Prikaz"));

		String[] opcije = {"Mesta", "Filijale", "Komitenti", "Racuni", "Transakcije", "Sve iz kopije", "Razlike"};
		JComboBox<String> cb = new JComboBox<>(opcije);

		Label labelRandom = new Label("/");
		TextField textRandom = new TextField();
		textRandom.setEnabled(false);

		Button buttonFind = new Button("Pretrazi");

		Panel topPanel = new Panel();
		topPanel.add(cb);
		topPanel.add(labelRandom);
		topPanel.add(textRandom);
		topPanel.add(buttonFind);

		cb.addActionListener(e -> {
			switch ((String) cb.getSelectedItem()) {
				case ("Mesta"):
				case ("Filijale"):
				case ("Komitenti"):
				case ("Sve iz kopije"):
				case ("Razlike"):
					labelRandom.setText("/");
					textRandom.setEnabled(false);
					break;
				case ("Racuni"):
					labelRandom.setText("Id Komitenta");
					textRandom.setEnabled(true);
					break;
				case ("Transakcije"):
					labelRandom.setText("Id Racuna");
					textRandom.setEnabled(true);
					break;
			}
			revalidate();
		});

		buttonFind.addActionListener(e -> {
			try {
				if (textRandom.isEnabled() && textRandom.getText().isEmpty())
					throw new Exception("Niste popunili sva polja!");
			} catch (Exception ex) {
				labelInfo.setForeground(Color.red);
				labelInfo.setText(ex.getMessage());
			}
		});

		ret.add(topPanel, BorderLayout.NORTH);
		ret.add(new Panel(), BorderLayout.CENTER);

		return ret;
	}

	public static void main(String[] args) throws IOException, Exception {
		new KorisnickaAplikacija();
		List<Mesto> m = (List<Mesto>) Connection.recieve("GetMestoAll");
		for (int i = 0; i < m.size(); i++) {
			System.out.println(m.get(i));
		}
		
		System.out.println("Test");
	}

}
