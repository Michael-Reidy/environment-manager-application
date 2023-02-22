import { ISettingMySuffix } from 'app/shared/model/setting-my-suffix.model';
import { IEnvironmentMySuffix } from 'app/shared/model/environment-my-suffix.model';

export interface INamespaceMySuffix {
  id?: number;
  name?: string | null;
  composedOfs?: INamespaceMySuffix[] | null;
  contains?: ISettingMySuffix[] | null;
  environment?: IEnvironmentMySuffix | null;
  namespace?: INamespaceMySuffix | null;
}

export const defaultValue: Readonly<INamespaceMySuffix> = {};
