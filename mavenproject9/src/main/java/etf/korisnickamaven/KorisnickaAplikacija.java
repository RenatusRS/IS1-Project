package etf.korisnickamaven;

import entiteti.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import retrofit2.Call;
import retrofit2.Response;

public class KorisnickaAplikacija extends Frame {

	Label labelInfo;
	JTable table = new JTable();

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
				if (textNaziv.getText().isEmpty() || textRandom.getText().isEmpty() || ((textKonekcija.isEnabled() && textKonekcija.getText().isEmpty()) && !((String) cb.getSelectedItem()).equals("Komitent")))
					throw new Exception("Niste popunili sva polja!");

				switch ((String) cb.getSelectedItem()) {
					case ("Mesto"):
						labelInfo.setText((String) Connection.recieve("CreateMesto", textNaziv.getText(), textRandom.getText()));
						break;
					case ("Komitent"):
						labelInfo.setText((String) Connection.recieve("CreateKomitent", textNaziv.getText(), textRandom.getText(), textKonekcija.getText()));
						break;
					case ("Filijala"):
						labelInfo.setText((String) Connection.recieve("CreateFilijala", textKonekcija.getText(), textNaziv.getText(), textRandom.getText()));
						break;
					case ("Komitent izmena"):
						labelInfo.setText((String) Connection.recieve("ChangeKomitentSediste", textNaziv.getText(), textRandom.getText()));
						break;
				}

				labelInfo.setForeground(Color.blue);

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

		JRadioButton radioUplata = new JRadioButton("Uplata", true);
		JRadioButton radioIsplata = new JRadioButton("Isplata");
		JRadioButton radioRazmena = new JRadioButton("Razmena");

		ButtonGroup bg = new ButtonGroup();
		bg.add(radioUplata);
		bg.add(radioIsplata);
		bg.add(radioRazmena);

		Panel radioPanel = new Panel(new GridLayout(3, 1));

		radioPanel.add(radioUplata);
		radioPanel.add(radioIsplata);
		radioPanel.add(radioRazmena);

		Panel inputPanel = new Panel(new GridLayout(2, 3));

		TextField textRacun1 = new TextField();
		TextField textIznos = new TextField();
		TextField textRacun2 = new TextField();

		Button buttonPotvrdi = new Button("Potvrdi");

		Label lastButton = new Label("Filijala");
		radioUplata.addActionListener(e -> lastButton.setText("Filijala"));
		radioIsplata.addActionListener(e -> lastButton.setText("Filijala"));
		radioRazmena.addActionListener(e -> lastButton.setText("Racun 2"));

		inputPanel.add(new Label("Racun 1"));
		inputPanel.add(new Label("Iznos"));
		inputPanel.add(lastButton);
		inputPanel.add(new Label());

		inputPanel.add(textRacun1);
		inputPanel.add(textIznos);
		inputPanel.add(textRacun2);
		inputPanel.add(buttonPotvrdi);

		ret.add(radioPanel, BorderLayout.WEST);
		ret.add(inputPanel, BorderLayout.CENTER);

		buttonPotvrdi.addActionListener(e -> {
			try {
				if (textRacun1.getText().isEmpty() || textIznos.getText().isEmpty() || textRacun2.getText().isEmpty())
					throw new Exception("Niste popunili sva polja!");

				if (radioUplata.isSelected()) {
					labelInfo.setText((String) Connection.recieve("CreateStavka", "U", textIznos.getText(), textRacun1.getText(), textRacun2.getText()));
				} else if (radioIsplata.isSelected()) {
					labelInfo.setText((String) Connection.recieve("CreateStavka", "I", textIznos.getText(), textRacun1.getText(), textRacun2.getText()));
				} else {
					labelInfo.setText((String) Connection.recieve("CreateStavka", "R", textIznos.getText(), textRacun1.getText(), textRacun2.getText()));
				}

				labelInfo.setForeground(Color.BLUE);

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
		Label labelMinus = new Label("Dozvoljeni minus");

		TextField textRandom = new TextField();
		TextField textFilijala = new TextField();
		TextField textMinus = new TextField();

		Panel panelCenter = new Panel(new GridLayout(4, 2));
		panelCenter.add(radioOpen);
		panelCenter.add(radioClose);
		panelCenter.add(labelRandom);
		panelCenter.add(labelFilijala);
		panelCenter.add(textRandom);
		panelCenter.add(textFilijala);
		panelCenter.add(labelMinus);
		panelCenter.add(textMinus);

		Button buttonAccept = new Button("Primeni");

		ret.add(panelCenter, BorderLayout.CENTER);
		ret.add(buttonAccept, BorderLayout.SOUTH);

		radioOpen.addActionListener(e -> {
			labelRandom.setText("ID Komitenta");
			labelFilijala.setText("ID Filijale");
			labelMinus.setText("Dozvoljeni minus");

			textFilijala.setEnabled(true);
			textMinus.setEnabled(true);
		});

		radioClose.addActionListener(e -> {
			labelRandom.setText("ID Racuna");
			labelFilijala.setText("/");
			labelMinus.setText("/");

			textFilijala.setEnabled(false);
			textMinus.setEnabled(false);
		});

		buttonAccept.addActionListener(e -> {
			try {
				if (textRandom.getText().isEmpty() || (textFilijala.isEnabled() && textFilijala.getText().isEmpty() || textMinus.getText().isEmpty()))
					throw new Exception("Niste popunili sva polja!");

				if (radioOpen.isSelected()) {
					labelInfo.setText((String) Connection.recieve("OpenRacun", textRandom.getText(), textFilijala.getText(), textMinus.getText()));
				} else {
					Connection.recieve("CloseRacun", textRandom.getText());
					labelInfo.setText("");
				}

				labelInfo.setForeground(Color.BLUE);
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

		Panel centerPanel = new Panel();
		JTextArea dataText = new JTextArea();
		centerPanel.add(dataText);

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
				if (textRandom.isEnabled() && textRandom.getText().isEmpty()) throw new Exception("Niste popunili sva polja!");

				String toAdd = new String();
				switch ((String) cb.getSelectedItem()) {
					case ("Mesta"):
						List<Mesto> m = (List<Mesto>) Connection.recieve("GetMestoAll");
						toAdd += "Kolone su: idMes Naziv Adresa\n\n";

						for (int i = 0; i < m.size(); i++) {
							toAdd += m.get(i).getIdMes().toString() + " ";
							toAdd += m.get(i).getNaziv() + " ";
							toAdd += m.get(i).getPostBr();
							toAdd += "\n";
						}

						break;
					case ("Komitenti"):
						List<Komitent> k = (List<Komitent>) Connection.recieve("GetKomitentAll");
						toAdd += "Kolone su: idK Naziv Adresa Sediste\n\n";

						for (Komitent kt : k) {
							toAdd += kt.getIdK() + " ";
							toAdd += kt.getNaziv() + " ";
							toAdd += kt.getAdresa() + " ";
							if (kt.getSediste() != null) toAdd += kt.getSediste();
							toAdd += "\n";
						}

						break;
					case ("Filijale"):
						List<Filijala> f = (List<Filijala>) Connection.recieve("GetFilijalaAll");
						toAdd += "Kolone su: idF Naziv Mesto Adresa\n\n";

						for (Filijala ft : f) {
							toAdd += ft.getIdFil() + " ";
							toAdd += ft.getNaziv() + " ";
							toAdd += ft.getMesto().getNaziv() + " ";
							toAdd += ft.getAdresa();
							toAdd += "\n";
						}

						break;
					case ("Racuni"):
						List<Racun> r = (List<Racun>) Connection.recieve("GetRacunKomitent", textRandom.getText());
						toAdd += "Kolone su: idRac Status Stanje DozvoljeniMinus BrojStavki Komitent Filijala DatumOtvaranja\n\n";

						for (Racun rt : r) {
							toAdd += rt.getIdRac() + " ";
							toAdd += rt.getStatus() + " ";
							toAdd += rt.getStanje() + " ";
							toAdd += rt.getDozvMinus() + " ";
							toAdd += rt.getBrojStavki() + " ";
							toAdd += rt.getKomitent() + " ";
							toAdd += rt.getFilijala() + " ";
							toAdd += rt.getDatum().toString();
							toAdd += "\n";
						}

						break;
					case ("Transakcije"):
						List<Stavka> s = (List<Stavka>) Connection.recieve("GetStavkaRacun", textRandom.getText());
						toAdd += "Kolone su: idK Naziv Adresa Sediste\n\n";

						for (Stavka st : s) {
							toAdd += st.getIdSta() + " ";
							toAdd += st.getRacun().getIdRac() + " ";
							toAdd += st.getDatum().toString() + " ";
							toAdd += st.getIznos() + " ";
							toAdd += st.getRedBroj() + " ";
							toAdd += st.getTip() + " ";
							if (st.getFilijala() != null) toAdd += st.getFilijala();
							toAdd += "\n";
						}

						break;
					case ("Sve iz kopije"):
						toAdd += (String) Connection.recieve("GetAll");
						
					case("Razlike"):
						String test = (String) Connection.recieve("GetDiff");
						System.out.println(test);
						toAdd += (String) Connection.recieve("GetDiff");
					default:
						break;
				}

				dataText.setText(toAdd);
				labelInfo.setText("");

			} catch (Exception ex) {
				labelInfo.setForeground(Color.red);
				labelInfo.setText(ex.getMessage());
			}
		});

		ret.add(topPanel, BorderLayout.NORTH);
		ret.add(centerPanel, BorderLayout.CENTER);

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
