package com.github.kokecena.conversordivisas.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.github.kokecena.conversordivisas.commons.KLFactory;
import com.github.kokecena.conversordivisas.components.QueryTextField;
import com.github.kokecena.conversordivisas.components.combobox.CurrencyComboBox;
import com.github.kokecena.conversordivisas.exchangerate.model.ExchangeRateCodes;
import com.github.kokecena.conversordivisas.exchangerate.service.ExchangeRateService;
import com.github.kokecena.conversordivisas.service.ExchangeService;
import io.avaje.config.Config;
import io.github.parubok.swingfx.beans.binding.Bindings;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import static io.github.parubok.fxprop.SwingPropertySupport.selectedItemProperty;
import static io.github.parubok.fxprop.SwingPropertySupport.textProperty;


/**
 * Actualizar
 * - Mejorar el codigo caquiña y nombres
 * <p>
 * Agregar
 * - Inyeccion de dependencia
 * - Historial
 * - Ultima actualizacion de divisas
 * - Banderas ???
 */
public class MainView extends JFrame {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeService service;
    private CurrencyComboBox mainCurrencyCb;
    private CurrencyComboBox secondaryCurrencyCb;
    private QueryTextField mainCurrencyText;
    private QueryTextField secondaryCurrencyText;
    private final String FIRST_CODE = Config.get("app.currency.first");
    private final String SECOND_CODE = Config.get("app.currency.second");
    private JLabel header;
    private JLabel center;
    private JLabel south;

    public MainView(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
        this.service = new ExchangeService(exchangeRateService);
        initComponents();
    }

    public void startApp() {
        setupComboBox().thenComposeAsync(unused -> service.updateExchangeRatesFrom(FIRST_CODE, SwingUtilities::invokeLater))
                .thenRunAsync(this::setupWindow)
                .thenAccept(unused -> {
                    mainCurrencyText.setText(Config.get("app.currency.start-value"));
                    service.setBaseCurrencyCode(SECOND_CODE);
                });
    }

    private void setupWindow() {
        pack();
        setTitle(Config.get("app.name").concat(" | ").concat(Config.get("app.version")));
        setIconImage(new FlatSVGIcon("icons/appIcon.svg").getImage());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private CompletableFuture<Void> setupComboBox() {
        return exchangeRateService.getSupportedCodes(SwingUtilities::invokeLater)
                .thenApply(ExchangeRateCodes::supportedCodes)
                .thenAccept(stringStringMap -> {
                    mainCurrencyCb.setModel(stringStringMap);
                    secondaryCurrencyCb.setModel(stringStringMap);
                    mainCurrencyCb.setCurrency(FIRST_CODE);
                    secondaryCurrencyCb.setCurrency(SECOND_CODE);
                });
    }

    private void initComponents() {
        mainCurrencyText = new QueryTextField();
        secondaryCurrencyText = new QueryTextField();
        secondaryCurrencyText.setEditable(false);
        mainCurrencyCb = new CurrencyComboBox();
        secondaryCurrencyCb = new CurrencyComboBox();
        header = new JLabel("XX XX es igual a", SwingConstants.LEFT);
        center = new JLabel("XX XX", SwingConstants.LEFT);
        south = new JLabel("7 abr, 8:43 p.m UTC", SwingConstants.LEFT);
        header.putClientProperty(FlatClientProperties.STYLE, "font: $h3.regular.font");
        center.putClientProperty(FlatClientProperties.STYLE, "font: bold $h1.regular.font");
        south.putClientProperty(FlatClientProperties.STYLE, "font: 95% $light.font");
        setLayout(new MigLayout("ins dialog"));
        JPanel panelHeader = new JPanel(new MigLayout("fillx, ins dialog"));
        panelHeader.add(header, "al center, north");
        panelHeader.add(center, "al center, center");
        panelHeader.add(south, "al center, south");
        add(panelHeader, "wrap");
        add(mainCurrencyText, "wmin 100, wmax 100, split 3");
        add(new JSeparator(SwingConstants.VERTICAL), "growy");
        add(mainCurrencyCb, "wmax 180, wmin 180, wrap");
        add(secondaryCurrencyText, "wmin 100, wmax 100, split 3");
        add(new JSeparator(SwingConstants.VERTICAL), "growy");
        add(secondaryCurrencyCb, "wmin 180, wmax 180");
        setupBinders();
        setupListeners();
    }

    private void setupBinders() {
        textProperty(header).bind(Bindings.createStringBinding(() -> {
            if (mainCurrencyCb.getSelectedCurrency().isPresent()) {
                return mainCurrencyText.getQuery()
                        .concat(" ")
                        .concat(mainCurrencyCb.getCurrentCurrency().code())
                        .concat(" es igual a");
            }
            return "";
        }, selectedItemProperty(mainCurrencyCb), mainCurrencyText.queryProperty()));
        textProperty(center).bind(Bindings.createStringBinding(() -> {
            if (secondaryCurrencyCb.getSelectedCurrency().isPresent()) {
                String code = secondaryCurrencyCb.getCurrentCurrency().code();
                return calculateExchange(code).toString()
                        .concat(" ")
                        .concat(code);
            }
            return "";
        }, selectedItemProperty(secondaryCurrencyCb), service.currentValueProperty(), mainCurrencyText.queryProperty()));
        textProperty(south).bind(Bindings.createStringBinding(() -> {
            LocalDateTime lastUpdate = service.getLastUpdate();
            if (lastUpdate == null) {
                return "";
            }
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd MMM, hh:mm a");
            return lastUpdate.format(pattern);
        }, service.lastUpdateProperty()));
    }

    private void setupListeners() {
        mainCurrencyText.addKeyListener(KLFactory.onKeyTyped(e -> {
            char c = e.getKeyChar();
            if (!(Character.isDigit(c) ||
                    (c == KeyEvent.VK_BACK_SPACE) ||
                    (c == KeyEvent.VK_DELETE) ||
                    (c == KeyEvent.VK_PERIOD) ||
                    (c == KeyEvent.VK_DECIMAL))) {
                e.consume();
            }
        }));
        service.baseCurrencyCodeProperty().addListener((o, oldCode, newCode) -> secondaryCurrencyText.setText(calculateExchange(newCode).toString()));
        mainCurrencyText.queryProperty().addListener((o, oldCode, newCode) -> secondaryCurrencyText.setText(calculateExchange(newCode).toString()));
        mainCurrencyCb.addActionListener(
                e -> service.updateExchangeRatesFrom(mainCurrencyCb.getCurrentCurrency().code(), SwingUtilities::invokeLater)
                        .thenAccept(unused -> mainCurrencyText.setText(Config.get("app.currency.start-value")))
        );
        secondaryCurrencyCb.addActionListener(e -> service.setBaseCurrencyCode(secondaryCurrencyCb.getCurrentCurrency().code()));
    }

    public BigDecimal calculateExchange(String code) {
        String currencyToExchange = mainCurrencyText.getQuery();
        boolean isEmptyOrBlankOrIsDot = currencyToExchange.isBlank() || currencyToExchange.isEmpty() || currencyToExchange.equals(".");
        double currValue = isEmptyOrBlankOrIsDot ? 0.0 : Double.parseDouble(currencyToExchange);
        return BigDecimal.valueOf(currValue * service.getCurrencyValue(code))
                .setScale(3, RoundingMode.CEILING);
    }

}