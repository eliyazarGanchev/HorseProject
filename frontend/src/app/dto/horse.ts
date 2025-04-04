import {Owner} from './owner';
import {Sex} from './sex';

export interface Horse {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  owner?: Owner;
  mother?: Horse;
  father?: Horse;
  image?: File;
  imageType?: string;
}

export interface HorseCreate {
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  ownerId?: number;
  motherId?: number;
  fatherId?: number;
  image?: File;
  imageType?: string;
}

export function convertFromHorseToCreate(horse: Horse): HorseCreate {
  return {
    name: horse.name,
    description: horse.description,
    dateOfBirth: horse.dateOfBirth,
    sex: horse.sex,
    ownerId: horse.owner?.id,
    motherId: horse.mother?.id,
    fatherId: horse.father?.id,
  };
}

export interface HorseUpdate {
  id: number;
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  ownerId?: number;
  motherId?: number;
  fatherId?: number;
  image?: File;
  imageType?: string;
}

export interface HorsePedigree {
  id: number;
  name: string;
  dateOfBirth: Date;
  sex: Sex;
  mother?: HorsePedigree;
  father?: HorsePedigree;
  expanded?: boolean;
}

export function convertFromHorseToUpdate(horse: Horse): HorseUpdate {
  return {
    id: horse.id!,
    name: horse.name,
    description: horse.description,
    dateOfBirth: horse.dateOfBirth,
    sex: horse.sex,
    ownerId: horse.owner ? horse.owner.id : undefined,
    motherId: horse.mother ? horse.mother.id : undefined,
    fatherId: horse.father ? horse.father.id : undefined,
  };
}
