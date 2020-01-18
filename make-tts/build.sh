#!/usr/bin/env zsh
say -o cat.aiff -v Daniel "cat"
say -o dog.aiff -v Daniel "dog"
say -o horse.aiff -v Daniel "horse"
say -o tiger.aiff -v Daniel "tiger"
say -o rabbit.aiff -v Daniel "rabbit"

say -o cat_pl.aiff -v Zosia "kot"
say -o dog_pl.aiff -v Zosia "pies"
say -o horse_pl.aiff -v Zosia "koń"
say -o tiger_pl.aiff -v Zosia "tygrys"
say -o rabbit_pl.aiff -v Zosia "królik"


say -o animals-vocab1-en_GB.aiff -v Daniel "Pets"
say -o animals-vocab2-en_GB.aiff -v Daniel "In the Zoo"

say -o animals-vocab1-pl_PL.aiff -v Zosia "Zwierzęta domowe"
say -o animals-vocab2-pl_PL.aiff -v Zosia "W zoo"

say -o animals-conv1-q-en_GB.aiff -v Daniel "Have you got a pet?"
say -o animals-conv1-a-en_GB.aiff -v Serena "Yes, I do."
say -o animals-conv2-q-en_GB.aiff -v Daniel "What is it?"
say -o animals-conv2-a-en_GB.aiff -v Serena "It’s a cat."
say -o animals-conv3-q-en_GB.aiff -v Daniel "What is your cat’s name?"
say -o animals-conv3-a-en_GB.aiff -v Serena "My cat’s name is Max ."

# say -o animals-conv1-q-pl_PL.aiff -v Zosia "Czy masz zwierzątko?"
# say -o animals-conv2-a-pl_PL.aiff -v Ewa "Tak, mam."
# say -o animals-conv3-q-pl_PL.aiff -v Zosia "Co to za zwierzątko?"
# say -o animals-conv4-a-pl_PL.aiff -v Ewa "To jest kot."
# say -o animals-conv5-q-pl_PL.aiff -v Zosia "Jak wabi się twój kot?"
# say -o animals-conv6-a-pl_PL.aiff -v Ewa "Mój kot wabi się Max."

for i in *.aiff; do
    ffmpeg -y -i "$i" -qscale:a 3 -ac 2 "${i[@]/%aiff/mp3}"
done
