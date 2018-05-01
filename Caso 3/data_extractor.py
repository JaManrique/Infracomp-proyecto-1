import os
from datetime import datetime

PERF_LOG = 'perfLogs/bbb.csv'
TESTS_FILE = 'testResults/log.txt'
RESULTS_FILE = 'results.res'

CPU = 'cpu'
MEMORY = 'memory'
NETWORK = 'network'

#5 hour difference between java millis and python formatting
TIME_DELTA = 5*60*60*1000 

def extract_from_test(test_line):
  data = {}
  info = test_line.split(',')
  data['start'] = int(info[0]) #+ TIME_DELTA
  data['end'] = int(info[1]) #+ TIME_DELTA
  data['test_type'] = info[2]
  data['key_creation_time'] = info[3]
  data['avg_update_time'] = info[4]
  data['failed_requests'] = info[5][:-1]
  return data

def append_result(data):
  result = ''
  for att in data:
    result += str(data[att]) + ','
  result = result[:-1] + '\n'
  with open(RESULTS_FILE, mode='a') as file:
    file.write(result)
    #Format: start, end, test_type, key_creation_time_avg, avg_update_time, failed_requests, %CPU_avg, %mem_avg, bytes_network
  
def validInterval(perf_log_data, test_data):
  sample_date = datetime.strptime(perf_log_data[0], '%m/%d/%Y %H:%M:%S.%f').timestamp()*1000
  print(test_data['start'], ' vs. ', sample_date, ' = ', test_data['start'] - sample_date)
  return test_data['start'] < sample_date and sample_date < test_data['end']

def extract_from_perf_logs(data):
  cpu_avg = 0
  memory = 0
  network_bytes = 0
  count = 0
  with open(PERF_LOG, mode='r') as file:
    alreadyRead = False
    metadata = file.readline() #Skipping first line
    for line in file:
      info = line.split(',')
      info[0] = info[0][1:-1]
      info[1] = info[1][1:-1]
      info[2] = info[2][1:-1]
      info[3] = info[3][1:-2]

      if validInterval(info, data):
        alreadyRead = True
        cpu_avg += float(info[2])
        print(info[3])
        memory += float(info[3])
        network_bytes += float(info[1])
        count += 1
      #Past certain line, there should not be more lines between the test interval
      if alreadyRead and not validInterval(info, data):
        break
  if count != 0:
    data[CPU] = cpu_avg/count
    data[MEMORY] = memory/count
    data[NETWORK] = network_bytes/count
  else:
    data[CPU] = 'undefined'
    data[MEMORY] = 'undefined'
    data[NETWORK] = 'undefined'

def extract_data():
  with open(TESTS_FILE) as file:
    for line in file:
      data = extract_from_test(line)
      extract_from_perf_logs(data)
      append_result(data)

extract_data()
