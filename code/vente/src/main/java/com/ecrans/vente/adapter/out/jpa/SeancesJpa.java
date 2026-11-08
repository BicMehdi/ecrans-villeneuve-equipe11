package com.ecrans.vente.adapter.out.jpa;

import com.ecrans.vente.application.port.out.ConflitDeVersion;
import com.ecrans.vente.application.port.out.Seances;
import com.ecrans.vente.domaine.commun.SeanceId;
import com.ecrans.vente.domaine.seance.Seance;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class SeancesJpa implements Seances {
    private final SeanceJpaRepository repository;

    public SeancesJpa(SeanceJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Seance> parId(SeanceId id) {
        return repository.findById(id.valeur()).map(SeanceEntite::versDomaine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seance> enVente() {
        return repository.findAll().stream().map(SeanceEntite::versDomaine).toList();
    }

    @Override
    @Transactional
    public void enregistrer(Seance seance) {
        var existante = repository.findById(seance.id().valeur());
        long actuelle = existante.map(SeanceEntite::versionDomaine).orElse(0L);
        if (seance.version() != actuelle) throw new ConflitDeVersion(seance.id(), seance.version(), actuelle);
        var entite = existante.orElseGet(() -> new SeanceEntite(seance));
        if (existante.isPresent()) entite.appliquer(seance);
        try {
            repository.saveAndFlush(entite);
        } catch (OptimisticLockingFailureException e) {
            throw new ConflitDeVersion(seance.id(), seance.version(), actuelle + 1);
        } catch (DataIntegrityViolationException e) {
            if (existante.isPresent()) throw e;
            throw new ConflitDeVersion(seance.id(), seance.version(), 1);
        }
    }
}
