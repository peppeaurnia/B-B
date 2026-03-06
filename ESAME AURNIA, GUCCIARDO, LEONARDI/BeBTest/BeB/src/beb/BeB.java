/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb;

import beb.facade.BnB;
import beb.dominio.Camera;
import beb.dominio.Prenotazione;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Main (Presentation Layer / Console UI).
 * Interagisce esclusivamente con il Facade (BnB) per testare l'applicativo.
 * È stata progettata per essere "stupida" (priva di logica di business),
 * demandando tutti i controlli e le operazioni al backend architetturale.
 */
public class BeB {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BnB bnb = BnB.getInstance();

        // 1. Database Seeding (Mock Data per collaudo)
        bnb.aggiungiCameraSistema(new Camera("Singola", 50.0f, 1, 5));
        bnb.aggiungiCameraSistema(new Camera("Matrimoniale", 80.0f, 2, 3));
        bnb.aggiungiCameraSistema(new Camera("Suite", 150.0f, 4, 1));

        System.out.println("-------------------------------------------------");
        System.out.println("        🏨 B&B SYSTEM - PATTERN EDITION 🏨       ");
        System.out.println("-------------------------------------------------");

        while (true) {
            System.out.println("\n--- MENU PRINCIPALE ---");
            System.out.println("1. Login Cliente");
            System.out.println("2. Registrazione Nuovo Cliente");
            System.out.println("3. Login Gestore");
            System.out.println("0. Esci dal Sistema");
            System.out.print("Seleziona un'operazione: ");

            int scelta = leggiIntero(scanner);
            if (scelta == 0) break;

            switch (scelta) {
                case 1:
                    System.out.print("Email: "); String emailC = scanner.nextLine();
                    System.out.print("Password: "); String passC = scanner.nextLine();
                    if (bnb.autenticazioneCliente(emailC, passC)) {
                        System.out.println("\n✅ Login effettuato con successo!");
                        menuCliente(scanner, bnb, emailC);
                    } else {
                        System.out.println("\n❌ Errore: Credenziali errate.");
                    }
                    break;
                case 2:
                    System.out.print("Nome Completo: "); String nomeRegC = scanner.nextLine();
                    System.out.print("Email: "); String emailRegC = scanner.nextLine();
                    System.out.print("Password: "); String passRegC = scanner.nextLine();
                    bnb.registraCliente(nomeRegC, emailRegC, passRegC);
                    System.out.println("\n✅ Cliente registrato con successo! Ora puoi fare il login.");
                    break;
                case 3:
                    System.out.print("Email: "); String emailG = scanner.nextLine();
                    System.out.print("Password: "); String passG = scanner.nextLine();
                    if (bnb.autenticazioneGestore(emailG, passG)) {
                        System.out.println("\n✅ Accesso Gestore consentito!");
                        menuGestore(scanner, bnb);
                    } else {
                        System.out.println("\n❌ Errore: Credenziali errate.");
                    }
                    break;

                default:
                    System.out.println("❌ Scelta non valida.");
            }
        }
        System.out.println("Chiusura del sistema in corso...");
        scanner.close();
    }

    // ------------------------------------------------------------------------
    // MENU SPECIFICI DEGLI ATTORI
    // ------------------------------------------------------------------------

    private static void menuCliente(Scanner scanner, BnB bnb, String email) {
        int mioId = bnb.getIdCliente(email);

        while (true) {
            System.out.println("\n-------------------------------------------------");
            System.out.println("👤 AREA CLIENTE (" + email + ")");
            System.out.println("-------------------------------------------------");
            System.out.println("1. Visualizza TUTTE le mie prenotazioni (Storico)");
            System.out.println("2. Richiedi una nuova Prenotazione");
            System.out.println("3. Paga una Prenotazione (APPROVATA)");
            System.out.println("4. Richiedi Modifica Date Prenotazione");
            System.out.println("5. Effettua il Check-In");
            System.out.println("6. Effettua il Check-Out");
            System.out.println("7. Cancella una tua Prenotazione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            int scelta = leggiIntero(scanner);
            if (scelta == 0) return;

            try {
                switch (scelta) {
                    case 1:
                        System.out.println("\n-- IL MIO STORICO PRENOTAZIONI --");
                        List<Prenotazione> miePrenotazioni = bnb.visualizzaTuttePrenotazioniCliente(email);
                        if (miePrenotazioni.isEmpty()) {
                            System.out.println("ℹ️ Non hai ancora effettuato prenotazioni.");
                        } else {
                            for (Prenotazione p : miePrenotazioni) stampaDettaglio(p);
                        }
                        break;

                    case 2:
                        System.out.println("\n-- NUOVA PRENOTAZIONE --");
                        System.out.print("Tipo Camera (Singola/Matrimoniale/Suite): "); String tipo = scanner.nextLine();
                        System.out.print("Data Inizio (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(scanner.nextLine());
                        System.out.print("Data Fine (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(scanner.nextLine());

                        Prenotazione p = bnb.richiediPrenotazione(tipo, start, end, mioId);
                        System.out.println("\n✅ Prenotazione Creata! Ecco i dettagli:");
                        stampaDettaglio(p);
                        break;

                    case 3:
                        System.out.println("\n-- PAGA PRENOTAZIONI APPROVATE --");
                        List<Prenotazione> daPagare = bnb.visualizzaRichiesteApprovate(email);

                        if (daPagare.isEmpty()) {
                            System.out.println("ℹ️ Non hai nessuna prenotazione in attesa di pagamento (Stato: APPROVATA).");
                            break;
                        }

                        for (Prenotazione r : daPagare) stampaDettaglio(r);

                        System.out.print("\nInserisci l'ID della Prenotazione da pagare: "); int idPaga = leggiIntero(scanner);
                        System.out.print("Numero Carta (16 cifre): "); String carta = scanner.nextLine();
                        System.out.print("CVV (3 cifre): "); int cvv = leggiIntero(scanner);

                        bnb.pagaRichiesta(idPaga, carta, cvv);
                        System.out.println("✅ Pagamento accettato! Lo stato è passato a: PRENOTATA");
                        break;

                    case 4:
                        System.out.println("\n-- MODIFICA DATE PRENOTAZIONE --");
                        List<Prenotazione> modificabili = bnb.visualizzaPrenotazioniPagateCliente(email);

                        if (modificabili.isEmpty()) {
                            System.out.println("ℹ️ Non hai prenotazioni modificabili. Devi prima averne una pagata (Stato: PRENOTATA).");
                            break;
                        }

                        for (Prenotazione pMod : modificabili) stampaDettaglio(pMod);

                        System.out.print("\nInserisci l'ID della prenotazione da modificare: "); int idMod = leggiIntero(scanner);
                        System.out.print("Nuova Data Inizio (YYYY-MM-DD): "); LocalDate nStart = LocalDate.parse(scanner.nextLine());
                        System.out.print("Nuova Data Fine (YYYY-MM-DD): "); LocalDate nEnd = LocalDate.parse(scanner.nextLine());

                        bnb.richiestaModificaPrenotazione(idMod, nStart, nEnd);
                        System.out.println("✅ Richiesta di modifica inviata al gestore. Stato attuale: MODIFICATA");
                        break;

                    case 5:
                        System.out.println("\n-- CHECK-IN --");
                        List<Prenotazione> checkins = bnb.visualizzaCheckInDisponibili(email);
                        if (checkins.isEmpty()) {
                            System.out.println("ℹ️ Nessun Check-in disponibile (Controlla che la prenotazione sia pagata e la data coincida).");
                        } else {
                            for (Prenotazione pr : checkins) stampaDettaglio(pr);
                            System.out.print("\nInserisci l'ID per confermare il Check-in: "); int idIn = leggiIntero(scanner);
                            bnb.confermaCheckIn(idIn);
                            System.out.println("✅ Check-in effettuato! Stato attuale: CHECK-IN");
                        }
                        break;

                    case 6:
                        System.out.println("\n-- CHECK-OUT --");
                        List<Prenotazione> checkouts = bnb.visualizzaCheckOutDisponibili(email);
                        if (checkouts.isEmpty()) {
                            System.out.println("ℹ️ Nessun Check-out disponibile oggi. (Il check-in deve essere fatto e la data di fine deve coincidere).");
                        } else {
                            for (Prenotazione pr : checkouts) stampaDettaglio(pr);
                            System.out.print("\nInserisci l'ID per confermare il Check-out: "); int idOut = leggiIntero(scanner);
                            bnb.confermaCheckOut(idOut);
                            System.out.println("✅ Check-out completato! Arrivederci. Stato attuale: CHECK-OUT");
                        }
                        break;

                    case 7:
                        System.out.println("\n-- CANCELLA PRENOTAZIONE --");
                        List<Prenotazione> cancellabili = bnb.visualizzaTuttePrenotazioniCliente(email);

                        if (cancellabili.isEmpty()) {
                            System.out.println("ℹ️ Non hai nessuna prenotazione a sistema.");
                            break;
                        }

                        for (Prenotazione pCanc : cancellabili) stampaDettaglio(pCanc);

                        System.out.print("\nInserisci l'ID della prenotazione da CANCELLARE: "); int idCanc = leggiIntero(scanner);

                        bnb.cancellaPrenotazioneCliente(idCanc);
                        System.out.println("✅ Prenotazione annullata. Stato attuale: CANCELLATA");
                        break;

                    default:
                        System.out.println("❌ Scelta non valida.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato data non valido. Usa il formato YYYY-MM-DD.");
            } catch (Exception e) {
                System.out.println("❌ ERRORE: " + e.getMessage());
            }
        }
    }

    private static void menuGestore(Scanner scanner, BnB bnb) {
        while (true) {
            System.out.println("\n-------------------------------------------------");
            System.out.println("⚙️ AREA GESTORE B&B");
            System.out.println("-------------------------------------------------");
            System.out.println("1. Visualizza TUTTE le Prenotazioni a sistema");
            System.out.println("2. Gestisci Richieste in ATTESA (Approva/Rifiuta)");
            System.out.println("3. Gestisci Richieste MODIFICATE");
            System.out.println("4. Cancella forzatamente una prenotazione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            int scelta = leggiIntero(scanner);
            if (scelta == 0) return;

            try {
                switch (scelta) {
                    case 1:
                        System.out.println("\n-- PANORAMICA GLOBALE PRENOTAZIONI --");
                        List<Prenotazione> tutte = bnb.visualizzaRichieste();
                        if (tutte.isEmpty()) {
                            System.out.println("ℹ️ Nessuna prenotazione registrata nel sistema.");
                        } else {
                            for (Prenotazione p : tutte) stampaDettaglio(p);
                        }
                        break;

                    case 2:
                        System.out.println("\n-- GESTIONE RICHIESTE IN ATTESA --");
                        List<Prenotazione> inAttesa = bnb.cercaPrenotazioniInAttesa();
                        if (inAttesa.isEmpty()) {
                            System.out.println("ℹ️ Nessuna richiesta in attesa di approvazione.");
                        } else {
                            for (Prenotazione p : inAttesa) stampaDettaglio(p);
                            System.out.print("\nInserisci l'ID della prenotazione da gestire (0 per uscire): ");
                            int idGest = leggiIntero(scanner);
                            if (idGest != 0) {
                                System.out.print("Vuoi APPROVARE (A) o CANCELLARE (C)? ");
                                String dec = scanner.nextLine().toUpperCase();
                                if (dec.equals("A")) {
                                    bnb.aggiornaRichiesta(idGest, "APPROVATA");
                                    System.out.println("✅ Richiesta approvata!");
                                } else if (dec.equals("C")) {
                                    bnb.aggiornaRichiesta(idGest, "CANCELLATA");
                                    System.out.println("✅ Richiesta cancellata!");
                                }
                            }
                        }
                        break;

                    case 3:
                        System.out.println("\n-- GESTIONE RICHIESTE MODIFICATE --");
                        List<Prenotazione> modificate = bnb.visualizzaRichiesteModificate();
                        if (modificate.isEmpty()) {
                            System.out.println("ℹ️ Nessuna modifica pendente.");
                        } else {
                            for (Prenotazione p : modificate) stampaDettaglio(p);
                            System.out.print("\nInserisci ID prenotazione da valutare: ");
                            int idMod = leggiIntero(scanner);
                            System.out.print("Accetti le nuove date? (S/N): ");
                            String risp = scanner.nextLine().toUpperCase();
                            bnb.aggiornaRichiestaModificata(idMod, risp.equals("S"));
                            System.out.println("✅ Valutazione registrata. (Stato aggiornato in PRENOTATA).");
                        }
                        break;

                    case 4:
                        System.out.print("\nInserisci l'ID della Prenotazione da eliminare dal sistema: ");
                        int idDel = leggiIntero(scanner);
                        bnb.cancellaPrenotazioneGestore(idDel);
                        System.out.println("✅ Prenotazione soppressa. Stato attuale: ELIMINATA");
                        break;

                    default:
                        System.out.println("❌ Scelta non valida.");
                }
            } catch (Exception e) {
                System.out.println("❌ ERRORE: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------------
    // UTILITIES DI PRESENTAZIONE (UI Helpers)
    // ------------------------------------------------------------------------

    /**
     * Stampa formattata a schermo dei dettagli di una prenotazione.
     */
    private static void stampaDettaglio(Prenotazione p) {
        System.out.println("-------------------------------------------------");
        System.out.println("🎫 ID Prenotazione : " + p.getIdPrenotazione() + " | ID Cliente: " + p.getIdCliente());
        System.out.println("🛏️ ID Camera       : " + p.getIdCamera());
        System.out.println("📅 Date           : Da " + p.getDataInizio() + " a " + p.getDataFine());
        System.out.println("💶 Totale         : € " + p.getPrezzoTotale());
        System.out.println("📌 STATO ATTUALE  : [" + p.getStatoNome() + "]");
        System.out.println("-------------------------------------------------");
    }

    /**
     * Metodo di sicurezza per evitare crash dovuti all'inserimento di stringhe
     * al posto di numeri interi nei menu.
     */
    private static int leggiIntero(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("❌ Inserisci un numero valido: ");
            }
        }
    }
}