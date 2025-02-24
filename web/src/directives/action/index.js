import user from './user'
import role from './role'
import Enum from './enum'
import log from './log'
import org from './org'
import param from './param'

export default {
  ...user,
  ...role,
  ...Enum,
  ...log,
  ...org,
  ...param,
}