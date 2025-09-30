package translation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.List;


// TODO Task D: Update the GUI for the program to align with UI shown in the README example.
//            Currently, the program only uses the CanadaTranslator and the user has
//            to manually enter the language code they want to use for the translation.
//            See the examples package for some code snippets that may be useful when updating
//            the GUI.
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Create instances of your converters
            CountryCodeConverter countryConverter = new CountryCodeConverter();
            LanguageCodeConverter languageConverter = new LanguageCodeConverter();

            // Create the proper translator (JSONTranslator instead of CanadaTranslator)
            Translator translator = new JSONTranslator();


            // LANGUAGE PANEL - JComboBox with language names
            JPanel languagePanel = new JPanel();
            languagePanel.add(new JLabel("Language:"));

            // Create JComboBox and populate with language names (not codes)
            JComboBox<String> languageComboBox = new JComboBox<>();

            // Get all language codes from translator and convert to names
            List<String> languageCodes = translator.getLanguageCodes();
            for (String code : languageCodes) {
                String languageName = languageConverter.fromLanguageCode(code);
                if (languageName != null) {
                    languageComboBox.addItem(languageName);
                }
            }

            languagePanel.add(languageComboBox);


            // TRANSLATION LABEL - Shows the translation result
            JPanel translationPanel = new JPanel();
            translationPanel.add(new JLabel("Translation:"));
            JLabel translationLabel = new JLabel("");
            translationPanel.add(translationLabel);


            // COUNTRY LIST - Scrollable JList showing all country names
            DefaultListModel<String> listModel = new DefaultListModel<>();
            JList<String> countryList = new JList<>(listModel);
            JScrollPane scrollPane = new JScrollPane(countryList);

            // Set preferred size for scroll pane
            scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));

            // Populate country list with country names (in English)
            List<String> countryCodes = translator.getCountryCodes();
            for (String code : countryCodes) {
                String countryName = countryConverter.fromCountryCode(code);
                if (countryName != null) {
                    listModel.addElement(countryName);
                }
            }


            // ACTION LISTENER - Update translation when country or language is selected
            ListSelectionListener updateTranslation = e -> {
                if (e.getValueIsAdjusting()) {
                    return; // Only handle final selection
                }

                String selectedCountry = countryList.getSelectedValue();
                String selectedLanguage = (String) languageComboBox.getSelectedItem();

                if (selectedCountry == null || selectedLanguage == null) {
                    translationLabel.setText("");
                    return;
                }

                // Convert country name to country code
                String countryCode = countryConverter.fromCountry(selectedCountry);

                // Convert language name to language code
                String languageCode = languageConverter.fromLanguage(selectedLanguage);

                if (countryCode == null || languageCode == null) {
                    translationLabel.setText("Translation not available");
                    return;
                }

                // Translate the selected country to the selected language
                String translation = translator.translate(countryCode, languageCode);

                if (translation != null) {
                    translationLabel.setText(translation);
                } else {
                    translationLabel.setText("Translation not available");
                }
            };

            // Add listener to country list
            countryList.addListSelectionListener(updateTranslation);

            // Add listener to language combo box
            languageComboBox.addActionListener(e -> {
                // Trigger translation update when language changes
                String selectedCountry = countryList.getSelectedValue();
                if (selectedCountry != null) {
                    updateTranslation.valueChanged(new javax.swing.event.ListSelectionEvent(
                            countryList, 0, 0, false));
                }
            });


            // MAIN PANEL - Arrange all components
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(translationPanel);
            mainPanel.add(scrollPane);


            // FRAME SETUP
            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}