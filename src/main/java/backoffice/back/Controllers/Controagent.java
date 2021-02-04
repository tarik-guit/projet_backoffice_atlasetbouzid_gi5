package backoffice.back.Controllers;

import backoffice.back.Entities.Admin;
import backoffice.back.Entities.Agent;
import backoffice.back.Repositories.Repoadmin;
import backoffice.back.Repositories.Repoagent;
import backoffice.back.envoieemail.MyConstants;
import backoffice.back.envoieemail.Sendemail;
import backoffice.back.requestetresponse.MessageResponse;
import backoffice.back.requestetresponse.SignupRequest;
import backoffice.back.securitycontroller.AuthController;
import backoffice.back.services.Createagent;
import backoffice.back.services.Supprimeragent;
import backoffice.back.services.genererpassword;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin()
public class Controagent {
   @Autowired
   private Sendemail send;
    @Autowired
    private Repoagent repagent;
    @Autowired
    private Createagent create;
    @Autowired
    private Supprimeragent supp;
    @Autowired
    private genererpassword gen;
    @Autowired
    public AuthController u;

    @GetMapping("/agents")
    public List<Agent> getallagents() {
        return repagent.findAll();
    }

    @GetMapping("/agents_currentadmin")
    public List<Agent> getallagentsforcurrentuser() {
        return repagent.getallforcurrentuser(u.getUsername());
    }

    @DeleteMapping("/agents")
    public String deleteall() {
        repagent.deleteAll();
        return "supprimés";

    }

    @DeleteMapping("/agent/{id}")
    public String deletbyid(@PathVariable Long id) {
        repagent.deleteById(id);
        return "supprimé";
    }

    @PostMapping("/agent")
    public MessageResponse creereagent(@RequestBody Agent agent) {
        Set<String> role= new HashSet<String>();
        role.add("user");
        String password=gen.generateCommonLangPassword();
        JSONObject json = create.createagentcompte(new SignupRequest(agent.getTel(),agent.getEmail(),role,password));
        if(json.get("message").toString().equals("User registered successfully!")){repagent.save(agent);
        //*****envoie email
            String subject="Ensa Pay (Demande de candidature)";
            String text="Ensa Pay: Bonjour Madame/Monsieur:"+agent.getNom()+" "+agent.getPrenom()+" vous etes admis votre mot de passe" +
                    "  est: "+password+" et le  username: "+agent.getTel()+" connectez vous et modifiez le pour plus de securité"+".Remarque: "+agent.getRemarque();
             String email=agent.getEmail();
            MyConstants my=new MyConstants(email,subject,text);
            send.sendSimpleEmail(my);
            // ********new MessageResponse("Error: Username is already taken!")

        return new MessageResponse("agent registered successfully!"); }
        if(json.get("message").toString().equals("Error: Username is already taken!")){return new MessageResponse("Error: Username is already taken!"); }
        if(json.get("message").toString().equals("Error: Email is already in use!")){return new MessageResponse("Error: Email is already in use!"); }

        return new MessageResponse("Agent not registred");
    }

    @PutMapping("/Agent/{id}")
    public Agent updateecole(@PathVariable Long id, @RequestBody Agent cla) {
        Agent agent= repagent.findById(id).get();
        if (cla.getNom() != null) {
            agent.setNom(cla.getNom());
        }
        if (cla.getPrenom() != null) {
            agent.setPrenom(cla.getPrenom());
        }

        if (cla.getTel() != null) {
            agent.setTel(cla.getTel());
        }
        if (cla.getEmail()!= null) {
            agent.setEmail(cla.getEmail());
        }
        if (cla.getNumcin() != null) {
            agent.setNumcin(cla.getNumcin());
        }

        return repagent.save(agent);
    }

    @DeleteMapping("/deleteagent/{username}")
    public MessageResponse supprimeragent(@PathVariable String username) {
       Agent agent=repagent.getagentbytel(username);
        JSONObject json = supp.supprimeragentcompte(username);

        if(json.get("message").toString().equals("A")){
           //*****envoie email
            String subject="Ensa Pay (Compte supprimé)";
            String text="Ensa Pay: Bonjour Madame/Monsieur:"+agent.getNom()+" "+agent.getPrenom()+" Votre compte a été supprimé vous pouver plus" +
                    " vous connecter a Ensa Pay ";
            String email=agent.getEmail();
            MyConstants my=new MyConstants(email,subject,text);
            send.sendSimpleEmail(my);
            // ********
            repagent.deleteById(agent.getId());
            return new MessageResponse("Agent deleted successfully!"); }
        return new MessageResponse("Agent not deleted");
    }

}
