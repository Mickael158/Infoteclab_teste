1- Clone les code : 
    _ Front-end "login"
    _ Back-end "back-login" 

2- Installation :
	_ PostgreSQL 
	_ Java 17
	_ React 19

3- Cree un database dans PostgreSQL nomme : login

4- Lancement :
	_ Back-end : 
		cree .env comment dans .env.exemple
		lancer par le commande "mvn spring-boot:run"
	_ Front-end :
		 cree .env comment dans .env.exemple
		lancer par le commande "npm run start"


_____________________________________________________________________________________________________________

Voir devriez insere (par insomnia ou postman) un nouveau utilisateur par l'URL API : http://localhost:8080/auth/register
ex:
	{
		"username" : Mick
		"mail" : razafindrakotomickael77@gmail.com
		"password" : 123456
		"role" : Admin
	}

Vous pouvez maintenant vous connecter dans le login de l'affichage

Vous pouvez vos deconnecter par le bouton "Log out" gauche-haute
