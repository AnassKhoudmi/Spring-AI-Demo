package net.khoudmi.peenset.model;

import java.util.List;

public record ResultatDepense(double salaire, List<Depense> depenses, double reste) {
}
