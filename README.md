# My Bachelor

## Erklärung der Pfadaufteilung
1. Pfade werden nach ihren Tags aufgeteilt (Artifacts, Rules, Contracts, _Messaging...) (jedem Pfad ist mindestens **ein** solcher Pfad zugeteilt)
2. Pfade werden in Primär-, Sekundär- und Interne Primärpfade aufgeteilt
- Primärpfad: ```/api/artifacts``` -> liefert ein/alle Elemente zurück der/die dargestellt werden können 
  (muss also eine GET Anfrage zulassen, kann auch andere Anfragen zulassen z.B. POST)
- Sekundärpfad: ```/api/artifacts/{id}``` -> liefert ein Element zurück, wenn es dazugehörigen Primärpfad gibt
  (muss GET Anfrage zulassen kann aber auch andere Anfragen wie DELETE zulassen)
- Interne Primärpfade: ```/api/artifacts/{id}/representations``` -> Liefert alle zugeordneten Objekte zu einem bestimmten Artifact zurück
  (muss GET Anfrage zulassen, kann auch andere Anfragen zulassen)
- Alle anderen Pfade werden als "normale" Pfade wie in Swagger angezeigt -> es gibt sie also, aber sie können nicht in einer MasterDetail View angezeigt werden

Unterscheidung zwischen MasterDetail- und List-View:
- Wenn alle Pfade, die zu einem Tag zugeordnet sind nur einen Primärpfad haben, einen Sekundärpfad und beliebig viele Interne Primärpfade (weobei alle Pfade dem Primärpfad unterstellt sein müssen), dann kann dieser Tag in einer MasterDetail View dargestellt werden
- Wenn es mehrere Primärpfade gibt, oder Pfade, die nicht einem Primärpfad zugeordnet werden können, dann wird dieser Tag als List-View angezeigt.




