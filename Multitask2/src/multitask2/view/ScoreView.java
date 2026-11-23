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

import multitask2.model.Model;

/**
 * Gestion de l'affichage du score
 */
public class ScoreView {
	
	/**
	 * Contexte graphique qui fournit la taille d'écran et réalise le rendu des
	 * images.
	 */
	 public final ApplicationContext context;
	 /** Largeur de l'écran relevée à la construction, en pixels. */
	 public final int width;
	 /** Hauteur de l'écran relevée à la construction, en pixels. */
	 public final int height;
	 /** Coordonnées du bouton "rejouer" du menu */
	 public final Rectangle rejouer;
	 /** Coordonnées du bouton "menu" du menu */
	 public final Rectangle menu;
	 
	 /**
	  * Crée la vue de l'affichage du score et stocke les données de l'écran (taille et coordonnées bouton).
	  * 
	  * @param context context graphique fourni par la bibliothèque Zen
	  * 
	  * @throws NullPointerException si {@code context} est {@code null}
	  */
	 public ScoreView(ApplicationContext context) {
		 Objects.requireNonNull(context);
		 this.context = context;
		 var screen = context.getScreenInfo();
		 height = screen.height();
		 width = screen.width();
		 rejouer = new Rectangle((int) (width / 2.5), (height / 5) * 2, width / 5, height / 10);
		 menu = new Rectangle((int) (width / 2.5), (int) ((height / 5) * 3), width / 5, height / 10);
	 }
	 
	 /**
	   * Gère la partie gestion de la souris et du clavier pour l'affichage du score
	   * 
	   * @param context contexte graphique utilisé pour le rendu
	   * 
	   * @return {@code true} si l'utilisateur veux jouer et {@code false} s'il veut revenir au menu
	   * 
	   * @throws NullPointerException si {@code context} est {@code null}
	   */
	  public static boolean renderScore(ApplicationContext context,Model model) {
		  Objects.requireNonNull(context);
		  
		  var score = new ScoreView(context);
		  
		  /* Affichage du Score */
		  score.context.renderFrame(graphics -> ScoreView.drawFinalScore(graphics, score, model));
		  
		  while (true) {
		        var event = context.pollEvent();
		        if (event == null) {
		          continue;
		        }
		        switch (event) {
		        /* événement souris */
				case PointerEvent s: {
					var mouse = s.location();
					
					if (score.rejouer.contains(mouse.x(), mouse.y())) {
						return true;
					}
					else if (score.menu.contains(mouse.x(), mouse.y())) {
						return false;
					}
					else {
						continue;
					}
				}
				/* événement clavier */
				case KeyboardEvent k: {
					if (k.key() == Key.R) {
						return true;
					}
					if (k.key() == Key.M) {
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
	   * @param score 
	   * @param model    modèle fournissant le temps écoulé
	   */
	  private static void drawFinalScore(Graphics2D graphics, ScoreView score, Model model) {
		  var height = score.height;
		  var width = score.width;
		  // Couleur pour le fond
		  graphics.setColor(GeneralView.BACKGROUND);
		  graphics.fillRect(0, 0, width, height);
		  
		  // Affichage du score
		  graphics.setColor(Color.WHITE);
		  graphics.setFont(graphics.getFont().deriveFont(Font.BOLD, 40f));
		  String text = String.format("Score : " + model.time());
		  graphics.drawString(text, (int) (width / 3), height / 5);
		  
		  // Bouton Rejouer
		  graphics.setColor(Color.WHITE);
		  GeneralView.centerText(graphics, "R pour Rejouer", score.rejouer);
		  
		  // Bouton retourné au menu
		  graphics.setColor(Color.WHITE);
		  GeneralView.centerText(graphics, "M pour retourné au Menu", score.menu);  
	  }
}
