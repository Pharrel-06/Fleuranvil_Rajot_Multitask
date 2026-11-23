package multitask2.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;
import com.github.forax.zen.KeyboardEvent.Key;

/**
 * Gestion du menu principal
 */
public class MainMenuView {
	
	/**
	 * Contexte graphique qui fournit la taille d'écran et réalise le rendu des
	 * images.
	 */
	 public final ApplicationContext context;
	 /** Largeur de l'écran relevée à la construction, en pixels. */
	 public final int width;
	 /** Hauteur de l'écran relevée à la construction, en pixels. */
	 public final int height;
	 /** Coordonnées du bouton "jouer" du menu */
	 public final Rectangle jouer;
	 /** Coordonnées du bouton "quitte" du menu */
	 public final Rectangle quitte;
	 
	 /**
	  * Crée la vue du menu principal et stocke les données de l'écran (taille et coordonnées bouton).
	  * 
	  * @param context context graphique fourni par la bibliothèque Zen
	  * 
	  * @throws NullPointerException si {@code context} est {@code null}
	  */
	 public MainMenuView(ApplicationContext context) {
		 Objects.requireNonNull(context);
		 this.context = context;
		 var screen = context.getScreenInfo();
		 height = screen.height();
		 width = screen.width();
		 jouer = new Rectangle((int) (width / 2.5), (height / 5) * 2, width / 5, height / 10);
		 quitte = new Rectangle((int) (width / 2.5), (int) ((height / 5) * 3), width / 5, height / 10);
	 }
	 
	 /**
	   * Gère la partie gestion de la souris et du clavier pour le menu principal
	   * 
	   * @param context contexte graphique utilisé pour le rendu
	   * 
	   * @return {@code true} si l'utilisateur veux jouer et {@code false} s'il veut quitter
	   * 
	   * @throws NullPointerException si {@code context} est {@code null}
	   */
	  public static boolean renderMainMenu(ApplicationContext context) {
		  Objects.requireNonNull(context);
		  
		  var menu = new MainMenuView(context);
		  
		  /* Affichage du menu */
		  menu.context.renderFrame(graphics -> MainMenuView.drawMainMenu(graphics, menu));
		  
		  while (true) {
		        var event = context.pollEvent();
		        if (event == null) {
		          continue;
		        }
		        switch (event) {
		        /* événement souris */
				case PointerEvent s: {
					var mouse = s.location();
					
					if (menu.jouer.contains(mouse.x(), mouse.y())) {
						return true;
					}
					else if (menu.quitte.contains(mouse.x(), mouse.y())) {
						return false;
					}
					else {
						continue;
					}
				}
				/* événement clavier */
				case KeyboardEvent k: {
					if (k.key() == Key.J) {
						return true;
					}
					if (k.key() == Key.Q) {
						return false;
					}
				}
			}
		 }
	  }
	 
	  /**
	   * Dessine le menu principal qui permet de lancer la partie ou de quitter directement
	   * 
	   * @param graphics surface graphique de l'image en cours
	   */
	  private static void drawMainMenu(Graphics2D graphics, MainMenuView menu) {
		  var height = menu.height;
		  var width = menu.width;
		  // Couleur pour le fond
		  graphics.setColor(GeneralView.BACKGROUND);
		  graphics.fillRect(0, 0, width, height);
		  
		  // Titre du menu principal
		  graphics.setColor(Color.WHITE);
		  graphics.setFont(graphics.getFont().deriveFont(Font.BOLD, 40f));
		  graphics.drawString("Menu principal Multitask 2", (int) (width / 3), height / 5);
		  
		  // Bouton Jouer
		  graphics.setColor(Color.WHITE);
		  GeneralView.centerText(graphics, "J pour Jouer", menu.jouer);
		  
		  // Bouton Quitter
		  graphics.setColor(Color.WHITE);
		  GeneralView.centerText(graphics, "Q pour Quitter", menu.quitte);  
	  }  
}
