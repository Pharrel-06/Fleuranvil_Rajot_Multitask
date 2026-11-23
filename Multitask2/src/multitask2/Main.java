package multitask2;

import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.Function;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.KeyboardEvent.Key;
import com.github.forax.zen.PointerEvent;

import multitask2.controller.Controller;
import multitask2.model.Game;
import multitask2.model.Model;
import multitask2.model.games.Balance;
import multitask2.model.games.Avoid;
import multitask2.view.GeneralView;
import multitask2.view.Viewers;
import multitask2.view.games.AvoidView;
import multitask2.view.games.BalanceView;
import multitask2.view.games.GameView;

/**
 * Point d'entrée de l'application de mini-jeux multitâches.
 *
 * <p>
 * Cette classe crée l'interface graphique, le modèle partagé par les mini-jeux
 * et le contrôleur qui orchestre les mises à jour et le rendu. Elle enregistre
 * les mini-jeux avec leur vue respective.
 * </p>
 */
public final class Main {

  /**
   * Constructeur privé afin d'empêcher l'instanciation de cette classe
   * utilitaire.
   */
  private Main() {
    super();
  }

  /**
   * Lance la boucle de l'application graphique.
   *
   * @param args arguments de la ligne de commande ; ils ne sont pas utilisés
   *             actuellement
   */
  public static void main(String[] args) {
    Application.run(GeneralView.BACKGROUND, Main::renderMainMenu);  
  }
  
  /**
   * Gère la partie gestion de la souris et du clavier pour le menu principal
   * 
   * @param context contexte graphique utilisé pour le rendu
   */
  public static void renderMainMenu(ApplicationContext context) {
	  context = Objects.requireNonNull(context);
	  var screen = context.getScreenInfo();
	  var height = screen.height();
	  var width = screen.width();

	  context.renderFrame(graphics -> GeneralView.drawMainMenu(graphics, width, height));
	  
	  
	  var playing = true;
	  var recJouer = new Rectangle((int) (width / 2.5), (height / 5) * 2, width / 5, height / 10);
	  var recQuitte = new Rectangle((int) (width / 2.5), (int) ((height / 5) * 3), width / 5, height / 10);
	  
	  while (playing) {
	        var event = context.pollEvent();
	        if (event == null) {
	          continue;
	        }
	        switch (event) {
			case PointerEvent s: {
				var mouse = s.location();
				if (Game.isInRectangle(mouse.x(), mouse.y(), recJouer)) {
					Main.game(context);
					playing = false;
				}
				else if (Game.isInRectangle(mouse.x(), mouse.y(), recQuitte)) {
					playing = false;
				}
				else {
					continue;
				}
				break;
			}
			case KeyboardEvent k: {
				if (k.key() == Key.J) {
					Main.game(context);
					playing = false;
		        	break;
				}
				if (k.key() == Key.Q) {
					playing = false;
		        	break;
				}
			}
			default:{}
			}
	  }
	  context.dispose();
  }

  /**
   * Construit les composants de la partie et exécute le contrôleur jusqu'à la fin
   * du jeu. <br>
   * Le contexte graphique est libéré même si l'exécution du contrôleur se termine
   * à la suite d'une exception.
   *
   * @param context contexte graphique fourni par la bibliothèque Zen
   */
  private static void game(ApplicationContext context) {
    var model = new Model();
    var viewers = new Viewers();
    // !!!!!!!!!!!!!!!! Pour remettre balance en premier, mettre temps à 0
    addGame(model, viewers, new Balance(), 0, BalanceView::new);
    addGame(model, viewers, new Avoid(), 5, AvoidView::new);
    addGame(model, viewers, new Avoid(), 10, AvoidView::new);

    var view = new GeneralView(context, viewers);
    var controller = new Controller(model, view, context);

    try {
      controller.run();
    } finally {
      context.dispose();
    }
  }

  /**
   * Enregistre un mini-jeu dans le modèle et associe à cette instance sa vue
   * graphique.
   *
   * @param <G>     type concret du mini-jeu
   * @param model   modèle auquel ajouter le mini-jeu
   * @param viewers registre dans lequel enregistrer sa vue
   * @param game    instance du mini-jeu à enregistrer
   * @param time    temps, en secondes depuis le début de la partie, auquel
   *                activer le mini-jeu
   * @param factory fonction construisant une vue à partir de l'instance du
   *                mini-jeu
   */
  private static <G extends Game> void addGame(Model model, Viewers viewers, G game, int time,
      Function<G, GameView> factory) {
    model.addGame(game, time);
    viewers.addGame(game, factory.apply(game));
  }
}
