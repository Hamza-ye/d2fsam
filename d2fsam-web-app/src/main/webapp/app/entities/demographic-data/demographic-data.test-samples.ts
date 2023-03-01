import dayjs from 'dayjs/esm';

import { DemographicDataLevel } from 'app/entities/enumerations/demographic-data-level.model';

import { IDemographicData, NewDemographicData } from './demographic-data.model';

export const sampleWithRequiredData: IDemographicData = {
  id: 98281,
  uid: 'index',
  date: dayjs('2022-11-20'),
};

export const sampleWithPartialData: IDemographicData = {
  id: 22347,
  uid: 'synthesizin',
  code: 'payment Idaho',
  created: dayjs('2022-11-20T18:54'),
  date: dayjs('2022-11-20'),
  femalePopulation: 39953,
  houses: 94691,
  healthFacilities: 96330,
  avgRoomArea: 13186,
  individualsPerHousehold: 75074,
};

export const sampleWithFullData: IDemographicData = {
  id: 41550,
  uid: 'Bedfordshir',
  code: 'silver Dram',
  created: dayjs('2022-11-20T17:22'),
  updated: dayjs('2022-11-20T17:20'),
  date: dayjs('2022-11-20'),
  level: DemographicDataLevel['SUBVILLAGE_LEVEL'],
  totalPopulation: 15167,
  malePopulation: 35421,
  femalePopulation: 13701,
  lessThan5Population: 29542,
  greaterThan5Population: 66740,
  bw5And15Population: 99955,
  greaterThan15Population: 73012,
  households: 67779,
  houses: 64610,
  healthFacilities: 50791,
  avgNoOfRooms: 27182,
  avgRoomArea: 95925,
  avgHouseArea: 16343,
  individualsPerHousehold: 87901,
  populationGrowthRate: 53261,
  comment: 'Idaho Avon firewall',
};

export const sampleWithNewData: NewDemographicData = {
  uid: 'primary Gra',
  date: dayjs('2022-11-20'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
